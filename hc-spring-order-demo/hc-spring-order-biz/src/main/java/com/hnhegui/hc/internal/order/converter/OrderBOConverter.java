package com.hnhegui.hc.internal.order.converter;

import com.hnhegui.hc.bo.order.OrderBO;
import com.hnhegui.hc.entity.order.Order;
import com.hnhegui.hc.feign.response.order.OrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 订单BO转换器
 */
@Mapper(componentModel = "spring")
public interface OrderBOConverter {

    OrderBOConverter INSTANCE = Mappers.getMapper(OrderBOConverter.class);

    // BO ↔ Entity
    OrderBO toBO(Order entity);
    Order toEntity(OrderBO bo);
    List<OrderBO> toBOList(List<Order> entities);

    // BO → DTO
    OrderDTO boToDTO(OrderBO bo);
    List<OrderDTO> boListToDTOList(List<OrderBO> boList);

    // DTO → BO
    OrderBO dtoToBO(OrderDTO dto);
}
