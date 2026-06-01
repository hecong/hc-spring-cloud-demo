package com.hnhegui.hc.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnhegui.hc.entity.order.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单商品明细 Mapper
 */
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 批量插入商品明细
     *
     * @param list 商品明细集合
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<OrderItem> list);

    /**
     * 批量插入或更新商品明细
     *
     * @param list 商品明细集合
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("list") List<OrderItem> list);
}
