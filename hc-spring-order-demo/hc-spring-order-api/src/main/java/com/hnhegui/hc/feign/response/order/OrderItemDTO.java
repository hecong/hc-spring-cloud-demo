package com.hnhegui.hc.feign.response.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单商品明细DTO
 */
@Data
public class OrderItemDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品图片
     */
    private String productImage;

    /**
     * 商品单价
     */
    private BigDecimal price;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 商品小计
     */
    private BigDecimal totalPrice;
}
