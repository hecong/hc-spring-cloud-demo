package com.hnhegui.hc.service.order.impl;

import com.hc.framework.mybatis.service.BaseServiceImpl;
import com.hnhegui.hc.bo.order.OrderBO;
import com.hnhegui.hc.entity.order.Order;
import com.hnhegui.hc.internal.order.converter.OrderBOConverter;
import com.hnhegui.hc.mapper.order.OrderMapper;
import com.hnhegui.hc.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单 Service 实现
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends BaseServiceImpl<OrderMapper, Order> implements OrderService {

    private final OrderBOConverter boConverter;

    @Override
    public List<OrderBO> listBO() {
        List<Order> entities = list();
        return boConverter.toBOList(entities);
    }

    @Override
    public OrderBO getBOById(Long id) {
        Order entity = getById(id);
        return boConverter.toBO(entity);
    }

    @Override
    public OrderBO getBOByOrderNo(String orderNo) {
        // TODO: 根据订单编号查询
        return null;
    }

    @Override
    public Long saveBO(OrderBO bo) {
        Order entity = boConverter.toEntity(bo);
        save(entity);
        return entity.getId();
    }

    @Override
    public void updateBO(OrderBO bo) {
        Order entity = boConverter.toEntity(bo);
        updateById(entity);
    }

    @Override
    public int insertBatch(List<Order> list) {
        return 0;
    }

    @Override
    public int insertOrUpdateBatch(List<Order> list) {
        return 0;
    }
}
