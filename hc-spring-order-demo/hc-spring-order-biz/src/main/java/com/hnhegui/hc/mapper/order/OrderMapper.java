package com.hnhegui.hc.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnhegui.hc.entity.order.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单 Mapper
 */
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 批量插入订单
     *
     * @param list 订单集合
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<Order> list);

    /**
     * 批量插入或更新订单
     *
     * @param list 订单集合
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("list") List<Order> list);
}
