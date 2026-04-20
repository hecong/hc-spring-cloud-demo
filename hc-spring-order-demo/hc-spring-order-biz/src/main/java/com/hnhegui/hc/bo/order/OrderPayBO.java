package com.hnhegui.hc.bo.order;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单支付记录BO
 */
@Data
public class OrderPayBO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 第三方支付单号
     */
    private String payNo;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付状态
     */
    private String payStatus;

    /**
     * 支付方式
     */
    private String payType;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;
}
