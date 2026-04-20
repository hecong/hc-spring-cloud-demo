package com.hnhegui.hc.entity.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单收货地址实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("order_address")
public class OrderAddress extends BaseEntity {

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
