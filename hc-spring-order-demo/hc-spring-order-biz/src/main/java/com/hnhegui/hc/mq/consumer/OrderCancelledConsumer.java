package com.hnhegui.hc.mq.consumer;

import com.hc.framework.rocketmq.core.BaseMqConsumer;
import com.hnhegui.hc.entity.order.OrderLog;
import com.hnhegui.hc.enums.OrderOperateTypeEnum;
import com.hnhegui.hc.mapper.order.OrderLogMapper;
import com.hnhegui.hc.mq.MqConstants;
import com.hnhegui.hc.mq.dto.OrderMessageDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.annotation.RocketMQMessageListener;
import org.springframework.stereotype.Component;

/**
 * 订单取消消费者 — 释放库存 / 退款处理
 *
 * <p>消费"订单取消"消息，执行取消后的清理工作：</p>
 * <ul>
 *   <li>记录取消操作日志</li>
 *   <li>释放被锁定的库存（TODO: 调用库存服务）</li>
 *   <li>已支付订单触发退款流程（TODO: 调用支付服务）</li>
 * </ul>
 *
 * <p><b>Topic/Tag/Group：</b></p>
 * <pre>
 *   Topic: OrderTopic
 *   Tag: cancelled
 *   ConsumerGroup: order-cancelled-consumer-group
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
    topic = MqConstants.TOPIC_TRANSACTION,
    tag = MqConstants.TAG_CANCELLED,
    consumerGroup = MqConstants.CONSUMER_GROUP_ORDER_CANCELLED
)
public class OrderCancelledConsumer extends BaseMqConsumer<OrderMessageDTO> {

    private final OrderLogMapper orderLogMapper;

    @Override
    protected void doConsume(OrderMessageDTO dto) {
        log.info("[订单取消消费者] 收到消息 orderNo={} userId={} remark={}",
            dto.getOrderNo(), dto.getUserId(), dto.getRemark());

        // ====== 业务处理（示例） ======

        // 1. 记录取消操作日志
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderNo(dto.getOrderNo());
        orderLog.setOperateType(OrderOperateTypeEnum.CANCEL.getCode());
        orderLog.setOperateDesc("订单已取消: " + (dto.getRemark() != null ? dto.getRemark() : ""));
        orderLogMapper.insert(orderLog);

        // 2. TODO: 释放库存（Feign 调用库存服务）
        // inventoryFeignClient.releaseStock(dto.getOrderNo());

        // 3. TODO: 已支付订单触发退款
        // if ("PAID".equals(dto.getOrderStatus())) {
        //     refundService.processRefund(dto.getOrderNo());
        // }

        log.info("[订单取消消费者] 处理完成 orderNo={}", dto.getOrderNo());
    }
}
