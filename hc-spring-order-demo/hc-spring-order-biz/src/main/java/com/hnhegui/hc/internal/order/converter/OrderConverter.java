package com.hnhegui.hc.internal.order.converter;

import com.hnhegui.hc.entity.order.Order;
import com.hnhegui.hc.feign.response.order.OrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 订单转换器
 */
@Mapper(componentModel = "spring")
public interface OrderConverter {

    OrderConverter INSTANCE = Mappers.getMapper(OrderConverter.class);

    OrderDTO toDTO(Order entity);

    List<OrderDTO> toDTOList(List<Order> entities);

    Order toEntity(OrderDTO dto);
}
