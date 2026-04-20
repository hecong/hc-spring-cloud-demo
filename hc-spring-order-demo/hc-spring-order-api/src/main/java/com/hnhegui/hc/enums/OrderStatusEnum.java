package com.hnhegui.hc.enums;

import lombok.Getter;

/**
 * 订单状态枚举
 */
@Getter
public enum OrderStatusEnum {

    /**
     * 待支付
     */
    PENDING_PAY("PENDING_PAY", "待支付"),

    /**
     * 已支付
     */
    PAID("PAID", "已支付"),

    /**
     * 已发货
     */
    DELIVERED("DELIVERED", "已发货"),

    /**
     * 已完成
     */
    COMPLETED("COMPLETED", "已完成"),

    /**
     * 已取消
     */
    CANCELLED("CANCELLED", "已取消"),

    /**
     * 退款中
     */
    REFUNDING("REFUNDING", "退款中"),

    /**
     * 已退款
     */
    REFUNDED("REFUNDED", "已退款");

    private final String code;
    private final String desc;

    OrderStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static OrderStatusEnum getByCode(String code) {
        for (OrderStatusEnum status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }
}
