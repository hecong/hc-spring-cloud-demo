package com.hnhegui.hc.service.order.impl;

import com.hc.framework.mybatis.service.BaseServiceImpl;
import com.hnhegui.hc.entity.order.OrderItem;
import com.hnhegui.hc.mapper.order.OrderItemMapper;
import com.hnhegui.hc.service.order.OrderItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单商品明细 Service 实现
 */
@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl extends BaseServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {
    @Override
    public int insertBatch(List<OrderItem> list) {
        return 0;
    }

    @Override
    public int insertOrUpdateBatch(List<OrderItem> list) {
        return 0;
    }
}
