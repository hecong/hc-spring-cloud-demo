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
 * 支付成功消费者 — 通知发货系统
 *
 * <p>消费"支付成功"消息，触发后续流程（如通知仓库发货、通知用户等）</p>
 *
 * <p>这是一个典型的<b>异步解耦场景</b>：支付完成后，通过 MQ 异步通知下游服务，
 * 避免支付接口直接依赖发货、短信等服务。</p>
 *
 * <p><b>Topic/Tag/Group：</b></p>
 * <pre>
 *   Topic: OrderTopic
 *   Tag: paid
 *   ConsumerGroup: order-paid-consumer-group
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MqConstants.TOPIC_TRANSACTION,
        tag = MqConstants.TAG_PAID,
        consumerGroup = MqConstants.CONSUMER_GROUP_ORDER_PAID
)
public class OrderPaidConsumer extends BaseMqConsumer<OrderMessageDTO> {

    private final OrderLogMapper orderLogMapper;

    @Override
    protected void doConsume(OrderMessageDTO dto) {
        log.info("[支付成功消费者] 收到消息 orderNo={} payAmount={} payType={}",
                dto.getOrderNo(), dto.getPayAmount(), dto.getPayType());

        // ====== 业务处理（示例） ======

        // 1. 记录支付操作日志
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderNo(dto.getOrderNo());
        orderLog.setOperateType(OrderOperateTypeEnum.PAY.getCode());
        orderLog.setOperateDesc(String.format("支付成功，金额: %s, 方式: %s",
                dto.getPayAmount(), dto.getPayType()));
        orderLogMapper.insert(orderLog);

        // 2. TODO: 通知仓库发货系统（Feign 调用或发送 WMS 消息）
        // warehouseFeignClient.createDeliveryOrder(dto.getOrderNo());

        // 3. TODO: 通知用户支付成功（短信/推送/邮件）
        // notifyService.notifyPaymentSuccess(dto.getUserId(), dto.getOrderNo());

        log.info("[支付成功消费者] 处理完成 orderNo={}", dto.getOrderNo());
    }
}
