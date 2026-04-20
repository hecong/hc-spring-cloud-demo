package com.hnhegui.hc.feign.response.order;

import com.hnhegui.hc.enums.PayStatusEnum;
import com.hnhegui.hc.enums.PayTypeEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单支付记录DTO
 */
@Data
public class OrderPayDTO {

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
    private PayStatusEnum payStatus;

    /**
     * 支付方式
     */
    private PayTypeEnum payType;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;
}
