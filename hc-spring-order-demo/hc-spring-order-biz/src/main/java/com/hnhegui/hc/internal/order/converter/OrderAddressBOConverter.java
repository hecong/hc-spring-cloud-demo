package com.hnhegui.hc.internal.order.converter;

import com.hnhegui.hc.bo.order.OrderAddressBO;
import com.hnhegui.hc.entity.order.OrderAddress;
import com.hnhegui.hc.feign.response.order.OrderAddressDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 订单收货地址BO转换器
 */
@Mapper(componentModel = "spring")
public interface OrderAddressBOConverter {

    OrderAddressBOConverter INSTANCE = Mappers.getMapper(OrderAddressBOConverter.class);

    // BO ↔ Entity
    OrderAddressBO toBO(OrderAddress entity);
    OrderAddress toEntity(OrderAddressBO bo);
    List<OrderAddressBO> toBOList(List<OrderAddress> entities);

    // BO → DTO
    OrderAddressDTO boToDTO(OrderAddressBO bo);
    List<OrderAddressDTO> boListToDTOList(List<OrderAddressBO> boList);

    // DTO → BO
    OrderAddressBO dtoToBO(OrderAddressDTO dto);
}
