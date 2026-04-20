package com.hnhegui.hc.entity.order;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hc.framework.mybatis.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单操作日志实体
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("order_log")
public class OrderLog extends BaseEntity {

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
