package com.hnhegui.hc.service.order.impl;

import com.hc.framework.mybatis.service.BaseServiceImpl;
import com.hc.framework.rocketmq.core.RocketMqSender;
import com.hc.framework.rocketmq.core.TransactionLogStore;
import com.hnhegui.hc.bo.order.OrderBO;
import com.hnhegui.hc.entity.order.Order;
import com.hnhegui.hc.entity.order.OrderLog;
import com.hnhegui.hc.entity.order.OrderPay;
import com.hnhegui.hc.enums.OrderOperateTypeEnum;
import com.hnhegui.hc.enums.OrderStatusEnum;
import com.hnhegui.hc.enums.PayStatusEnum;
import com.hnhegui.hc.internal.order.converter.OrderBOConverter;
import com.hnhegui.hc.mapper.order.OrderLogMapper;
import com.hnhegui.hc.mapper.order.OrderMapper;
import com.hnhegui.hc.mapper.order.OrderPayMapper;
import com.hnhegui.hc.mq.MqConstants;
import com.hnhegui.hc.mq.dto.OrderMessageDTO;
import com.hnhegui.hc.service.order.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 订单 Service 实现（MQ 集成版）
 *
 * <p>引入 RocketMQ 后的改动：</p>
 * <ul>
 *   <li>创建订单 → 发送普通消息 + 延迟消息</li>
 *   <li>支付订单 → 事务消息（支付成功通知）</li>
 *   <li>取消订单 → 事务消息（取消后释放库存/退款）</li>
 *   <li>发货订单 → 顺序消息</li>
 * </ul>
 */
@Slf4j
@Service
public class OrderServiceImpl extends BaseServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderBOConverter boConverter;
    private final OrderPayMapper orderPayMapper;
    private final OrderLogMapper orderLogMapper;
    private final RocketMqSender rocketMqSender;
    private final TransactionTemplate transactionTemplate;
    private final TransactionLogStore transactionLogStore;

    public OrderServiceImpl(OrderBOConverter boConverter,
                            OrderPayMapper orderPayMapper,
                            OrderLogMapper orderLogMapper,
                            RocketMqSender rocketMqSender,
                            TransactionTemplate transactionTemplate,
                            TransactionLogStore transactionLogStore) {
        this.boConverter = boConverter;
        this.orderPayMapper = orderPayMapper;
        this.orderLogMapper = orderLogMapper;
        this.rocketMqSender = rocketMqSender;
        this.transactionTemplate = transactionTemplate;
        this.transactionLogStore = transactionLogStore;
    }

    @Override
    public List<OrderBO> listBO() {
        List<Order> entities = list();
        return boConverter.toBOList(entities);
    }

    @Override
    public OrderBO getBOById(Long id) {
        Order entity = getById(id);
        return boConverter.toBO(entity);
    }

    @Override
    public OrderBO getBOByOrderNo(String orderNo) {
        Order entity = baseMapper.selectByOrderNo(orderNo);
        return boConverter.toBO(entity);
    }

    @Override
    public Long saveBO(OrderBO bo) {
        Order entity = boConverter.toEntity(bo);
        save(entity);
        return entity.getId();
    }

    @Override
    public void updateBO(OrderBO bo) {
        Order entity = boConverter.toEntity(bo);
        updateById(entity);
    }

    @Override
    public int insertBatch(List<Order> list) {
        return baseMapper.insertBatch(list);
    }

    @Override
    public int insertOrUpdateBatch(List<Order> list) {
        return baseMapper.insertOrUpdateBatch(list);
    }

    // ==================== MQ 集成方法 ====================

    @Override
    public String createOrderWithMQ(OrderBO bo) {
        // 1. 生成订单编号
        String orderNo = generateOrderNo();
        bo.setOrderNo(orderNo);
        bo.setOrderStatus(OrderStatusEnum.PENDING_PAY.getCode());

        // 2. 保存订单（事务）
        transactionTemplate.executeWithoutResult(status -> {
            Order entity = boConverter.toEntity(bo);
            baseMapper.insert(entity);
            log.info("[订单创建] 订单已保存 orderNo={} userId={} amount={}",
                    orderNo, bo.getUserId(), bo.getPayAmount());
        });

        // 3. 发送普通消息（异步通知：记录操作日志）
        OrderMessageDTO messageDTO = buildMessageDTO("CREATE", orderNo, bo);
        rocketMqSender.sendAsync(MqConstants.TOPIC_ORDER_NORMAL, MqConstants.TAG_CREATE, messageDTO);
        log.info("[订单创建] 已发送创建消息 orderNo={}", orderNo);

        // 4. 发送延迟消息（30分钟后检查支付超时）
        OrderMessageDTO timeoutDTO = buildMessageDTO("TIMEOUT", orderNo, bo);
        rocketMqSender.sendDelay(MqConstants.TOPIC_ORDER_DELAY, MqConstants.TAG_TIMEOUT, timeoutDTO,
                30, TimeUnit.MINUTES);
        log.info("[订单创建] 已发送超时检查消息（30分钟后） orderNo={}", orderNo);

        return orderNo;
    }

    @Override
    public void payOrder(String orderNo, String payType, BigDecimal payAmount) {

        // 1. 查询订单
        Order order = baseMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在: " + orderNo);
        }

        // 2. 构建支付消息
        OrderMessageDTO dto = new OrderMessageDTO();
        dto.setEventType("PAY");
        dto.setOrderNo(orderNo);
        dto.setPayAmount(payAmount);
        dto.setPayType(payType);
        dto.setEventTime(LocalDateTime.now());

        // 3. 发送事务消息 + 执行本地事务（框架自动 commit/rollback）
        rocketMqSender.sendTransaction(
                MqConstants.TOPIC_TRANSACTION, MqConstants.TAG_PAID, dto,
                ctx -> transactionTemplate.executeWithoutResult(status -> {
                    // 同一事务内持久化消息日志（供通用 Checker 回查）
                    transactionLogStore.save(ctx.message());

                    // 写入支付记录
                    OrderPay orderPay = new OrderPay();
                    orderPay.setOrderNo(orderNo);
                    orderPay.setPayNo(generatePayNo());
                    orderPay.setPayAmount(payAmount);
                    orderPay.setPayStatus(PayStatusEnum.SUCCESS.getCode());
                    orderPay.setPayType(payType);
                    orderPay.setPayTime(LocalDateTime.now());
                    orderPayMapper.insert(orderPay);

                    // 更新订单状态
                    order.setOrderStatus(OrderStatusEnum.PAID.getCode());
                    order.setPayType(payType);
                    order.setPayAmount(payAmount);
                    order.setPayTime(LocalDateTime.now());
                    baseMapper.updateById(order);

                    // 记录操作日志
                    OrderLog orderLog = new OrderLog();
                    orderLog.setOrderNo(orderNo);
                    orderLog.setOperateType(OrderOperateTypeEnum.PAY.getCode());
                    orderLog.setOperateDesc("支付成功，金额: " + payAmount);
                    orderLogMapper.insert(orderLog);
                }));
        log.info("[订单支付] 事务已提交 orderNo={}", orderNo);
    }

    @Override
    public void cancelOrder(String orderNo, String remark) {
        // 1. 构建取消消息
        OrderMessageDTO dto = new OrderMessageDTO();
        dto.setEventType("CANCEL");
        dto.setOrderNo(orderNo);
        dto.setRemark(remark);
        dto.setEventTime(LocalDateTime.now());

        // 2. 发送事务消息 + 执行本地事务（框架自动 commit/rollback）
        rocketMqSender.sendTransaction(
                MqConstants.TOPIC_TRANSACTION, MqConstants.TAG_CANCELLED, dto,
                ctx -> transactionTemplate.executeWithoutResult(status -> {
                    // 同一事务内持久化消息日志（供通用 Checker 回查）
                    transactionLogStore.save(ctx.message());

                    Order order = baseMapper.selectByOrderNo(orderNo);
                    if (order == null) {
                        throw new RuntimeException("订单不存在: " + orderNo);
                    }
                    order.setOrderStatus(OrderStatusEnum.CANCELLED.getCode());
                    order.setCancelTime(LocalDateTime.now());
                    baseMapper.updateById(order);

                    // 记录操作日志
                    OrderLog orderLog = new OrderLog();
                    orderLog.setOrderNo(orderNo);
                    orderLog.setOperateType(OrderOperateTypeEnum.CANCEL.getCode());
                    orderLog.setOperateDesc(remark != null ? remark : "用户取消订单");
                    orderLogMapper.insert(orderLog);
                }));
        log.info("[订单取消] 事务已提交 orderNo={}", orderNo);
    }

    @Override
    public void deliverOrder(String orderNo) {
        Order order = baseMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new RuntimeException("订单不存在: " + orderNo);
        }
        if (!OrderStatusEnum.PAID.getCode().equals(order.getOrderStatus())) {
            throw new RuntimeException("只有已支付订单才能发货，当前状态: " + order.getOrderStatus());
        }
        // 1. 更新订单状态
        transactionTemplate.executeWithoutResult(status -> {

            order.setOrderStatus(OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(LocalDateTime.now());
            baseMapper.updateById(order);

            // 记录操作日志
            OrderLog orderLog = new OrderLog();
            orderLog.setOrderNo(orderNo);
            orderLog.setOperateType(OrderOperateTypeEnum.DELIVERY.getCode());
            orderLog.setOperateDesc("订单已发货");
            orderLogMapper.insert(orderLog);
        });

        // 2. 发送顺序消息（同一订单的消息按序处理）
        OrderMessageDTO dto = new OrderMessageDTO();
        dto.setEventType("DELIVER");
        dto.setOrderNo(orderNo);
        dto.setEventTime(LocalDateTime.now());

        // messageGroup = orderNo，保证同一订单的操作串行消费
        rocketMqSender.sendOrderly(MqConstants.TOPIC_ORDER_FIFO, MqConstants.TAG_DELIVER, dto, orderNo);
        log.info("[订单发货] 已发送顺序消息 orderNo={}", orderNo);
    }

    // ==================== 私有工具方法 ====================

    /**
     * 生成订单编号
     */
    private String generateOrderNo() {
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "ORD" + timestamp + uuid;
    }

    /**
     * 生成支付单号
     */
    private String generatePayNo() {
        long timestamp = System.currentTimeMillis();
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "PAY" + timestamp + uuid;
    }

    /**
     * 构建消息 DTO
     */
    private OrderMessageDTO buildMessageDTO(String eventType, String orderNo, OrderBO bo) {
        OrderMessageDTO dto = new OrderMessageDTO();
        dto.setEventType(eventType);
        dto.setOrderNo(orderNo);
        dto.setUserId(bo.getUserId());
        dto.setOrderStatus(bo.getOrderStatus());
        dto.setPayAmount(bo.getPayAmount());
        dto.setPayType(bo.getPayType());
        dto.setEventTime(LocalDateTime.now());
        dto.setRemark(bo.getRemark());
        return dto;
    }
}
