package com.hnhegui.hc.mapper.order;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnhegui.hc.entity.order.OrderPay;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单支付记录 Mapper
 */
public interface OrderPayMapper extends BaseMapper<OrderPay> {

    /**
     * 批量插入支付记录
     *
     * @param list 支付记录集合
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<OrderPay> list);

    /**
     * 批量插入或更新支付记录
     *
     * @param list 支付记录集合
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("list") List<OrderPay> list);

    /**
     * 根据订单编号查询支付记录
     *
     * @param orderNo 订单编号
     * @return 支付记录
     */
    default OrderPay selectByOrderNo(String orderNo) {
        return selectOne(new LambdaQueryWrapper<OrderPay>()
                .eq(OrderPay::getOrderNo, orderNo));
    }
}
