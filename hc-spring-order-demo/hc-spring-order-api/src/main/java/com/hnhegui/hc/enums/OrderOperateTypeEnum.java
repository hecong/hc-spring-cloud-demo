package com.hnhegui.hc.enums;

import lombok.Getter;

/**
 * 订单操作类型枚举
 */
@Getter
public enum OrderOperateTypeEnum {

    /**
     * 创建
     */
    CREATE("CREATE", "创建"),

    /**
     * 支付
     */
    PAY("PAY", "支付"),

    /**
     * 取消
     */
    CANCEL("CANCEL", "取消"),

    /**
     * 发货
     */
    DELIVERY("DELIVERY", "发货"),

    /**
     * 完成
     */
    COMPLETE("COMPLETE", "完成");

    private final String code;
    private final String desc;

    OrderOperateTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据code获取枚举
     */
    public static OrderOperateTypeEnum getByCode(String code) {
        for (OrderOperateTypeEnum type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}
