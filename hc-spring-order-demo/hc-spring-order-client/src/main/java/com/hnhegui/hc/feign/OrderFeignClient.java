package com.hnhegui.hc.feign;

import com.hnhegui.hc.feign.response.order.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单服务 Feign 客户端 - 供内部服务调用
 * 返回原始数据，不使用 Result 包装
 */
@FeignClient(name = "order-service", fallbackFactory = OrderFeignClientFallbackFactory.class)
public interface OrderFeignClient {

    /**
     * 列出所有订单
     *
     * @return 订单列表
     */
    @GetMapping("/api/internal/order/list")
    List<OrderDTO> listOrders();

    /**
     * 根据ID获取订单
     *
     * @param id 订单ID
     * @return 订单
     */
    @GetMapping("/api/internal/order/get/{id}")
    OrderDTO getOrderById(@PathVariable("id") Long id);

    /**
     * 根据订单编号获取订单
     *
     * @param orderNo 订单编号
     * @return 订单
     */
    @GetMapping("/api/internal/order/getByOrderNo/{orderNo}")
    OrderDTO getOrderByOrderNo(@PathVariable("orderNo") String orderNo);

    /**
     * 保存订单
     *
     * @param dto 订单DTO
     * @return 订单ID
     */
    @PostMapping("/api/internal/order/save")
    Long saveOrder(@RequestBody OrderDTO dto);

    /**
     * 更新订单
     *
     * @param dto 订单DTO
     */
    @PutMapping("/api/internal/order/update")
    void updateOrder(@RequestBody OrderDTO dto);

    /**
     * 删除订单
     *
     * @param id 订单ID
     */
    @DeleteMapping("/api/internal/order/delete/{id}")
    void deleteOrder(@PathVariable("id") Long id);
}
