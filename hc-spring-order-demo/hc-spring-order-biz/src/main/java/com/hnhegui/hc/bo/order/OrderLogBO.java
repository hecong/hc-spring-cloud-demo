package com.hnhegui.hc.bo.order;

import lombok.Data;

/**
 * 订单操作日志BO
 */
@Data
public class OrderLogBO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 操作描述
     */
    private String operateDesc;
}
