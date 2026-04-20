package com.hnhegui.hc.internal.order.converter;

import com.hnhegui.hc.bo.order.OrderPayBO;
import com.hnhegui.hc.entity.order.OrderPay;
import com.hnhegui.hc.feign.response.order.OrderPayDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 订单支付记录BO转换器
 */
@Mapper(componentModel = "spring")
public interface OrderPayBOConverter {

    OrderPayBOConverter INSTANCE = Mappers.getMapper(OrderPayBOConverter.class);

    // BO ↔ Entity
    OrderPayBO toBO(OrderPay entity);
    OrderPay toEntity(OrderPayBO bo);
    List<OrderPayBO> toBOList(List<OrderPay> entities);

    // BO → DTO
    OrderPayDTO boToDTO(OrderPayBO bo);
    List<OrderPayDTO> boListToDTOList(List<OrderPayBO> boList);

    // DTO → BO
    OrderPayBO dtoToBO(OrderPayDTO dto);
}
