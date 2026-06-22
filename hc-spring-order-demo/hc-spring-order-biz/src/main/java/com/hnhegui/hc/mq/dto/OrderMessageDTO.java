package com.hnhegui.hc.mq.dto;

import com.hc.framework.common.util.JsonUtils;
import com.hc.framework.rocketmq.core.BaseMqMessage;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单消息 DTO（MQ 消息中的业务数据载体）
 */
@Data
public class OrderMessageDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 事件类型：CREATE / PAY / CANCEL / TIMEOUT / DELIVER */
    private String eventType;
    /** 订单编号 */
    private String orderNo;
    /** 用户 ID */
    private Long userId;
    /** 订单状态 */
    private String orderStatus;
    /** 支付金额 */
    private BigDecimal payAmount;
    /** 支付方式 */
    private String payType;
    /** 事件时间 */
    private LocalDateTime eventTime;
    /** 备注 */
    private String remark;

    /**
     * 从 BaseMqMessage.data 提取业务 DTO（Checker 和 Consumer 通用）。
     *
     * @deprecated 框架已支持 {@code BaseTransactionChecker<T>} 泛型自动反序列化
     *             和 {@link BaseMqMessage#getDataAs(Class)}，请直接使用泛型 Checker，
     *             不再需要手动调用此方法。
     */
    @Deprecated
    public static OrderMessageDTO from(BaseMqMessage msg) {
        Object data = msg.getData();
        if (data instanceof java.util.Map) {
            return JsonUtils.fromMap((java.util.Map<?, ?>) data, OrderMessageDTO.class);
        }
        return JsonUtils.fromJson(JsonUtils.toJson(data), OrderMessageDTO.class);
    }
}
