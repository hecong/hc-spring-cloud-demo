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
 * 订单发货消费者 — 顺序消费
 *
 * <p>使用 RocketMQ 顺序消息（FIFO），相同 orderNo 的消息按发送顺序消费，
 * 保证发货、取消等操作不会乱序执行。</p>
 *
 * <p><b>原理：</b>发送时使用 {@code sendOrderly("OrderTopic", "deliver", dto, orderNo)}
 * 指定 messageGroup = orderNo，同一订单的所有操作进入同一队列串行消费。</p>
 *
 * <p><b>Topic/Tag/Group：</b></p>
 * <pre>
 *   Topic: OrderTopic
 *   Tag: deliver
 *   ConsumerGroup: order-deliver-consumer-group
 * </pre>
 *
 * <p><b>消费模式：</b>需要配置为 FIFO 消费模式，确保同一 messageGroup 内消息顺序执行</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MqConstants.TOPIC_ORDER_FIFO,
        tag = MqConstants.TAG_DELIVER,
        consumerGroup = MqConstants.CONSUMER_GROUP_ORDER_DELIVER
)
public class OrderDeliverConsumer extends BaseMqConsumer<OrderMessageDTO> {

    private final OrderLogMapper orderLogMapper;

    @Override
    protected void doConsume(OrderMessageDTO dto) {
        log.info("[订单发货消费者] 收到消息 orderNo={} userId={}", dto.getOrderNo(), dto.getUserId());

        // 记录发货操作日志
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderNo(dto.getOrderNo());
        orderLog.setOperateType(OrderOperateTypeEnum.DELIVERY.getCode());
        orderLog.setOperateDesc("订单发货通知已接收");

        // ====== 业务处理（示例） ======

        // TODO: 调用物流平台创建运单
        // logisticsFeignClient.createShipment(dto.getOrderNo());

        // TODO: 通知用户已发货
        // notifyService.notifyOrderDelivered(dto.getUserId(), dto.getOrderNo());

        orderLogMapper.insert(orderLog);
        log.info("[订单发货消费者] 处理完成 orderNo={}", dto.getOrderNo());
    }
}
