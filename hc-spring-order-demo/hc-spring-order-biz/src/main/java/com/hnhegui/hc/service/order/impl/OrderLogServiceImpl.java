package com.hnhegui.hc.service.order.impl;

import com.hc.framework.mybatis.service.BaseServiceImpl;
import com.hnhegui.hc.entity.order.OrderLog;
import com.hnhegui.hc.mapper.order.OrderLogMapper;
import com.hnhegui.hc.service.order.OrderLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单操作日志 Service 实现
 */
@Service
@RequiredArgsConstructor
public class OrderLogServiceImpl extends BaseServiceImpl<OrderLogMapper, OrderLog> implements OrderLogService {
    @Override
    public int insertBatch(List<OrderLog> list) {
        return 0;
    }

    @Override
    public int insertOrUpdateBatch(List<OrderLog> list) {
        return 0;
    }
}
