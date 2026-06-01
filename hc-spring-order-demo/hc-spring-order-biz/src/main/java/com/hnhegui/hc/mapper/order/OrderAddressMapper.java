package com.hnhegui.hc.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnhegui.hc.entity.order.OrderAddress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单收货地址 Mapper
 */
public interface OrderAddressMapper extends BaseMapper<OrderAddress> {

    /**
     * 批量插入收货地址
     *
     * @param list 收货地址集合
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<OrderAddress> list);

    /**
     * 批量插入或更新收货地址
     *
     * @param list 收货地址集合
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("list") List<OrderAddress> list);
}
