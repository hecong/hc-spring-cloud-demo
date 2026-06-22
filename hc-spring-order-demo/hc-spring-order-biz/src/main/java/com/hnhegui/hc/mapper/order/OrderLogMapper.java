package com.hnhegui.hc.mapper.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    /**
     * 根据订单编号查询操作日志列表
     *
     * @param orderNo 订单编号
     * @return 操作日志列表
     */
    default List<OrderLog> selectListByOrderNo(String orderNo) {
        return selectList(new LambdaQueryWrapper<OrderLog>()
                .eq(OrderLog::getOrderNo, orderNo)
                .orderByDesc(OrderLog::getCreateTime));
    }
}
