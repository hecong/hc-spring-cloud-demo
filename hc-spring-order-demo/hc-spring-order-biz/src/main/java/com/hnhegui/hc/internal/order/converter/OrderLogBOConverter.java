package com.hnhegui.hc.internal.order.converter;

import com.hnhegui.hc.bo.order.OrderLogBO;
import com.hnhegui.hc.entity.order.OrderLog;
import com.hnhegui.hc.feign.response.order.OrderLogDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 订单操作日志BO转换器
 */
@Mapper(componentModel = "spring")
public interface OrderLogBOConverter {

    OrderLogBOConverter INSTANCE = Mappers.getMapper(OrderLogBOConverter.class);

    // BO ↔ Entity
    OrderLogBO toBO(OrderLog entity);
    OrderLog toEntity(OrderLogBO bo);
    List<OrderLogBO> toBOList(List<OrderLog> entities);

    // BO → DTO
    OrderLogDTO boToDTO(OrderLogBO bo);
    List<OrderLogDTO> boListToDTOList(List<OrderLogBO> boList);

    // DTO → BO
    OrderLogBO dtoToBO(OrderLogDTO dto);
}
