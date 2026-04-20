package com.hnhegui.hc.entity.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单支付记录实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("order_pay")
public class OrderPay extends BaseEntity {

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
