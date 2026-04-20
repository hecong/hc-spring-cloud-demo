package com.hnhegui.hc.feign;

import com.hnhegui.hc.feign.response.order.OrderDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * OrderFeignClient 降级工厂
 */
@Slf4j
@Component
public class OrderFeignClientFallbackFactory implements FallbackFactory<OrderFeignClient> {

    @Override
    public OrderFeignClient create(Throwable cause) {
        log.error("OrderFeignClient 调用失败: {}", cause.getMessage());
        return new OrderFeignClient() {
            @Override
            public List<OrderDTO> listOrders() {
                return Collections.emptyList();
            }

            @Override
            public OrderDTO getOrderById(Long id) {
                return null;
            }

            @Override
            public OrderDTO getOrderByOrderNo(String orderNo) {
                return null;
            }

            @Override
            public Long saveOrder(OrderDTO dto) {
                return null;
            }

            @Override
            public void updateOrder(OrderDTO dto) {
            }

            @Override
            public void deleteOrder(Long id) {
            }
        };
    }
}
