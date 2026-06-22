package com.hnhegui.hc.mq.consumer;

import com.hc.framework.rocketmq.core.BaseMqConsumer;
import com.hc.framework.rocketmq.core.RocketMqSender;
import com.hc.framework.rocketmq.core.TransactionLogStore;
import com.hnhegui.hc.entity.order.Order;
import com.hnhegui.hc.enums.OrderStatusEnum;
import com.hnhegui.hc.mapper.order.OrderMapper;
import com.hnhegui.hc.mq.MqConstants;
import com.hnhegui.hc.mq.dto.OrderMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 订单超时消费者 — 延迟消息触发
 *
 * <p><b>流程说明：</b></p>
 * <ol>
 *   <li>创建订单时，同时发送一条延迟消息（如 30 分钟后投递）</li>
 *   <li>本消费者收到延迟消息后，检查订单状态</li>
 *   <li>如果订单仍为 PENDING_PAY，说明用户超时未支付</li>
 *   <li>发送事务消息触发取消流程</li>
 * </ol>
 *
 * <p><b>Topic/Tag/Group：</b></p>
 * <pre>
 *   Topic: OrderTopic
 *   Tag: timeout
 *   ConsumerGroup: order-timeout-consumer-group
 * </pre>
 *
 * <p><b>注意：</b>延迟消息的可靠性和精确度取决于 RocketMQ 配置，生产环境建议
 * 结合定时任务做二次兜底检查，避免延迟消息丢失导致订单永远不超时取消。</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MqConstants.TOPIC_ORDER_DELAY,
        tag = MqConstants.TAG_TIMEOUT,
        consumerGroup = MqConstants.CONSUMER_GROUP_ORDER_TIMEOUT
)
public class OrderTimeoutConsumer extends BaseMqConsumer<OrderMessageDTO> {

    private final OrderMapper orderMapper;
    private final RocketMqSender rocketMqSender;
    private final TransactionLogStore transactionLogStore;

    @Override
    protected void doConsume(OrderMessageDTO dto) {
        String orderNo = dto.getOrderNo();
        log.info("[订单超时消费者] 收到延迟消息 orderNo={}", orderNo);

        // 1. 查询订单当前状态
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            log.warn("[订单超时消费者] 订单不存在 orderNo={}", orderNo);
            return;
        }

        // 2. 检查是否仍为待支付状态
        if (!OrderStatusEnum.PENDING_PAY.getCode().equals(order.getOrderStatus())) {
            log.info("[订单超时消费者] 订单已处理，跳过 orderNo={} status={}",
                    orderNo, order.getOrderStatus());
            return;
        }

        // 3. 超时 → 发送事务消息触发取消
        log.info("[订单超时消费者] 订单超时未支付，触发取消流程 orderNo={}", orderNo);

        OrderMessageDTO cancelDto = new OrderMessageDTO();
        cancelDto.setEventType("TIMEOUT");
        cancelDto.setOrderNo(orderNo);
        cancelDto.setUserId(order.getUserId());
        cancelDto.setOrderStatus(order.getOrderStatus());
        cancelDto.setEventTime(LocalDateTime.now());
        cancelDto.setRemark("超时未支付，系统自动取消");

        // 发送事务消息 + 执行本地事务（框架自动 commit/rollback）
        rocketMqSender.sendTransaction(
                MqConstants.TOPIC_TRANSACTION, MqConstants.TAG_CANCELLED, cancelDto,
                ctx -> {
                    // 同一事务内持久化消息日志（供通用 Checker 回查）
                    transactionLogStore.save(ctx.getMessage());
                    order.setOrderStatus(OrderStatusEnum.CANCELLED.getCode());
                    order.setCancelTime(LocalDateTime.now());
                    orderMapper.updateById(order);
                });
        log.info("[订单超时消费者] 取消事务已提交 orderNo={}", orderNo);
    }
}
