package com.hnhegui.hc.service.order.impl;

import com.hc.framework.mybatis.service.BaseServiceImpl;
import com.hnhegui.hc.entity.order.OrderAddress;
import com.hnhegui.hc.mapper.order.OrderAddressMapper;
import com.hnhegui.hc.service.order.OrderAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单收货地址 Service 实现
 */
@Service
@RequiredArgsConstructor
public class OrderAddressServiceImpl extends BaseServiceImpl<OrderAddressMapper, OrderAddress> implements OrderAddressService {
    @Override
    public int insertBatch(List<OrderAddress> list) {
        return 0;
    }

    @Override
    public int insertOrUpdateBatch(List<OrderAddress> list) {
        return 0;
    }
}
