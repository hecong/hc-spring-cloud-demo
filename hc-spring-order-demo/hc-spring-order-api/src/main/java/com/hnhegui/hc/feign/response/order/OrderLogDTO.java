package com.hnhegui.hc.feign.response.order;

import com.hnhegui.hc.enums.OrderOperateTypeEnum;
import lombok.Data;

/**
 * 订单操作日志DTO
 */
@Data
public class OrderLogDTO {

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
    private OrderOperateTypeEnum operateType;

    /**
     * 操作描述
     */
    private String operateDesc;
}
