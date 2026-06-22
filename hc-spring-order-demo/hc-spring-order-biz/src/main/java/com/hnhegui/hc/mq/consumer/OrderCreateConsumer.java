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
 * 订单创建消费者 — 记录操作日志
 *
 * <p>消费"订单创建"消息，将操作记录写入 order_log 表</p>
 *
 * <p><b>Topic/Tag/Group 示例：</b></p>
 * <pre>
 *   Topic: OrderTopic
 *   Tag: create
 *   ConsumerGroup: order-create-consumer-group
 * </pre>
 *
 * <p><b>幂等说明：</b>框架层 msgId 幂等 + 业务层 orderNo 唯一索引兜底</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RocketMQMessageListener(
        topic = MqConstants.TOPIC_ORDER_NORMAL,
        tag = MqConstants.TAG_CREATE,
        consumerGroup = MqConstants.CONSUMER_GROUP_ORDER_CREATE
)
public class OrderCreateConsumer extends BaseMqConsumer<OrderMessageDTO> {

    private final OrderLogMapper orderLogMapper;

    @Override
    protected void doConsume(OrderMessageDTO dto) {
        log.info("[订单创建消费者] 收到消息 orderNo={} userId={}", dto.getOrderNo(), dto.getUserId());

        // 记录操作日志
        OrderLog orderLog = new OrderLog();
        orderLog.setOrderNo(dto.getOrderNo());
        orderLog.setOperateType(OrderOperateTypeEnum.CREATE.getCode());
        orderLog.setOperateDesc("订单创建成功，订单编号: " + dto.getOrderNo());
        orderLogMapper.insert(orderLog);

        log.info("[订单创建消费者] 操作日志已记录 orderNo={}", dto.getOrderNo());
    }
}
