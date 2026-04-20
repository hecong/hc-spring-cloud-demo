package com.hnhegui.hc.internal.order.converter;

import com.hnhegui.hc.bo.order.OrderItemBO;
import com.hnhegui.hc.entity.order.OrderItem;
import com.hnhegui.hc.feign.response.order.OrderItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 订单商品明细BO转换器
 */
@Mapper(componentModel = "spring")
public interface OrderItemBOConverter {

    OrderItemBOConverter INSTANCE = Mappers.getMapper(OrderItemBOConverter.class);

    // BO ↔ Entity
    OrderItemBO toBO(OrderItem entity);
    OrderItem toEntity(OrderItemBO bo);
    List<OrderItemBO> toBOList(List<OrderItem> entities);

    // BO → DTO
    OrderItemDTO boToDTO(OrderItemBO bo);
    List<OrderItemDTO> boListToDTOList(List<OrderItemBO> boList);

    // DTO → BO
    OrderItemBO dtoToBO(OrderItemDTO dto);
}
