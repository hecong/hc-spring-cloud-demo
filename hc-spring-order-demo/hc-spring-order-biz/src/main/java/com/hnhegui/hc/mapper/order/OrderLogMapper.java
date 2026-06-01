package com.hnhegui.hc.mapper.order;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnhegui.hc.entity.order.OrderLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单操作日志 Mapper
 */
public interface OrderLogMapper extends BaseMapper<OrderLog> {

    /**
     * 批量插入操作日志
     *
     * @param list 操作日志集合
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<OrderLog> list);

    /**
     * 批量插入或更新操作日志
     *
     * @param list 操作日志集合
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("list") List<OrderLog> list);
}
