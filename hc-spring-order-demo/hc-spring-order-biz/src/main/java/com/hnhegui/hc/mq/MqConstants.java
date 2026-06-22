package com.hnhegui.hc.mq;

/**
 * RocketMQ 常量定义
 *
 * <p>所有 Topic、Tag、ConsumerGroup、TemplateBean 名称集中管理</p>
 *
 * <p><b>Topic 规划（RocketMQ 5.x 不同类型消息需要独立 Topic）：</b></p>
 * <pre>
 * 普通消息 → order-normal-topic        → create
 * 延时消息 → order-delay-topic         → timeout
 * 顺序消息 → order-fifo-topic          → deliver
 * 事务消息 → transaction-order-demo-topic → paid, cancelled
 * </pre>
 */
public final class MqConstants {

    private MqConstants() {
    }

    // ==================== Topic ====================

    /** 普通消息 Topic */
    public static final String TOPIC_ORDER_NORMAL = "order-normal-topic";

    /** 延时消息 Topic（RocketMQ 需创建为 DELAY 类型） */
    public static final String TOPIC_ORDER_DELAY = "order-delay-topic";

    /** 顺序消息 Topic（RocketMQ 需创建为 FIFO 类型） */
    public static final String TOPIC_ORDER_FIFO = "order-fifo-topic";

    /** 事务消息 Topic（支付、取消） */
    public static final String TOPIC_TRANSACTION = "transaction-order-demo-topic";

    // ==================== Tag ====================

    public static final String TAG_CREATE = "create";
    public static final String TAG_PAID = "paid";
    public static final String TAG_CANCELLED = "cancelled";
    public static final String TAG_TIMEOUT = "timeout";
    public static final String TAG_DELIVER = "deliver";

    // ==================== ConsumerGroup ====================

    public static final String CONSUMER_GROUP_ORDER_CREATE = "order-create-consumer-group";
    public static final String CONSUMER_GROUP_ORDER_PAID = "order-paid-consumer-group";
    public static final String CONSUMER_GROUP_ORDER_CANCELLED = "order-cancelled-consumer-group";
    public static final String CONSUMER_GROUP_ORDER_TIMEOUT = "order-timeout-consumer-group";
    public static final String CONSUMER_GROUP_ORDER_DELIVER = "order-deliver-consumer-group";

}
