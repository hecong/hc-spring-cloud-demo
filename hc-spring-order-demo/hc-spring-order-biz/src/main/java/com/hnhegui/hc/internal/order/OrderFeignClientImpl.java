package com.hnhegui.hc.internal.order;

import com.hnhegui.hc.bo.order.OrderBO;
import com.hnhegui.hc.feign.OrderFeignClient;
import com.hnhegui.hc.feign.response.order.OrderDTO;
import com.hnhegui.hc.internal.order.converter.OrderBOConverter;
import com.hnhegui.hc.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 订单服务内部接口实现 - 供 Feign 客户端调用
 * 实现 OrderFeignClient 接口，返回原始数据，不使用 Result 包装
 */
@RestController
@RequiredArgsConstructor
public class OrderFeignClientImpl implements OrderFeignClient {

    private final OrderService orderService;
    private final OrderBOConverter boConverter;

    @Override
    public List<OrderDTO> listOrders() {
        List<OrderBO> boList = orderService.listBO();
        return boConverter.boListToDTOList(boList);
    }

    @Override
    public OrderDTO getOrderById(@PathVariable("id") Long id) {
        OrderBO bo = orderService.getBOById(id);
        return boConverter.boToDTO(bo);
    }

    @Override
    public OrderDTO getOrderByOrderNo(@PathVariable("orderNo") String orderNo) {
        OrderBO bo = orderService.getBOByOrderNo(orderNo);
        return boConverter.boToDTO(bo);
    }

    @Override
    public Long saveOrder(OrderDTO dto) {
        OrderBO bo = boConverter.dtoToBO(dto);
        return orderService.saveBO(bo);
    }

    @Override
    public void updateOrder(OrderDTO dto) {
        OrderBO bo = boConverter.dtoToBO(dto);
        orderService.updateBO(bo);
    }

    @Override
    public void deleteOrder(@PathVariable("id") Long id) {
        orderService.removeById(id);
    }
}
