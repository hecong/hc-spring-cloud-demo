package com.hnhegui.hc.service.order;

import com.hc.framework.mybatis.service.BaseService;
import com.hnhegui.hc.bo.order.OrderBO;
import com.hnhegui.hc.entity.order.Order;

import java.util.List;

/**
 * 订单 Service 接口
 */
public interface OrderService extends BaseService<Order> {

    /**
     * 查询所有订单（返回BO）
     */
    List<OrderBO> listBO();

    /**
     * 根据ID查询订单（返回BO）
     */
    OrderBO getBOById(Long id);

    /**
     * 根据订单编号查询订单（返回BO）
     */
    OrderBO getBOByOrderNo(String orderNo);

    /**
     * 保存订单（接收BO）
     */
    Long saveBO(OrderBO bo);

    /**
     * 更新订单（接收BO）
     */
    void updateBO(OrderBO bo);
}
