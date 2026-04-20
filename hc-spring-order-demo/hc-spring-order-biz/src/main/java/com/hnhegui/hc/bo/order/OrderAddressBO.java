package com.hnhegui.hc.bo.order;

import lombok.Data;

/**
 * 订单收货地址BO
 */
@Data
public class OrderAddressBO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 收件人
     */
    private String receiver;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区县
     */
    private String area;

    /**
     * 详细地址
     */
    private String detailAddress;
}
