package com.hnhegui.hc.service.order.impl;

import com.hc.framework.mybatis.service.BaseServiceImpl;
import com.hnhegui.hc.entity.order.OrderPay;
import com.hnhegui.hc.mapper.order.OrderPayMapper;
import com.hnhegui.hc.service.order.OrderPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单支付记录 Service 实现
 */
@Service
@RequiredArgsConstructor
public class OrderPayServiceImpl extends BaseServiceImpl<OrderPayMapper, OrderPay> implements OrderPayService {
    @Override
    public int insertBatch(List<OrderPay> list) {
        return 0;
    }

    @Override
    public int insertOrUpdateBatch(List<OrderPay> list) {
        return 0;
    }
}
