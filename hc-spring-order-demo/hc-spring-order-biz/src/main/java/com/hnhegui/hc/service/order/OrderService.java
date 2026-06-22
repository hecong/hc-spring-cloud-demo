package com.hnhegui.hc.service.order;

import com.hc.framework.mybatis.service.BaseService;
import com.hnhegui.hc.bo.order.OrderBO;
import com.hnhegui.hc.entity.order.Order;

import java.util.List;

/**
 * 订单 Service 接口
 */
public interface OrderService extends BaseService<Order> {

    /**
     * 查询所有订单（返回BO）
     */
    List<OrderBO> listBO();

    /**
     * 根据ID查询订单（返回BO）
     */
    OrderBO getBOById(Long id);

    /**
     * 根据订单编号查询订单（返回BO）
     */
    OrderBO getBOByOrderNo(String orderNo);

    /**
     * 保存订单（接收BO）
     */
    Long saveBO(OrderBO bo);

    /**
     * 更新订单（接收BO）
     */
    void updateBO(OrderBO bo);

    // ==================== MQ 集成方法 ====================

    /**
     * 创建订单（MQ 集成版）
     *
     * <p>执行流程：</p>
     * <ol>
     *   <li>保存订单到数据库（订单 + 商品明细 + 收货地址）</li>
     *   <li>发送普通消息到 OrderTopic:create（消费者记录操作日志）</li>
     *   <li>发送延迟消息到 OrderTopic:timeout（30分钟后检查是否支付超时）</li>
     * </ol>
     *
     * @param bo 订单 BO
     * @return 订单编号
     */
    String createOrderWithMQ(OrderBO bo);

    /**
     * 支付订单（事务消息）
     *
     * <p>使用事务消息保证"支付记录写入"与"消息投递"的原子性：</p>
     * <ol>
     *   <li>先发送半消息（half message）</li>
     *   <li>执行本地事务：写入 order_pay + 更新 order_info 状态</li>
     *   <li>本地事务成功 → commit → 消息投递给消费者</li>
     *   <li>本地事务失败 → rollback → 消息不投递</li>
     *   <li>commit 前崩溃 → RocketMQ 回调 OrderPayChecker 回查</li>
     * </ol>
     *
     * @param orderNo   订单编号
     * @param payType   支付方式
     * @param payAmount 支付金额
     */
    void payOrder(String orderNo, String payType, java.math.BigDecimal payAmount);

    /**
     * 取消订单（事务消息）
     *
     * <p>使用事务消息保证"订单状态更新"与"消息投递"的原子性。
     * 成功后消费者释放库存、触发退款等。</p>
     *
     * @param orderNo 订单编号
     * @param remark  取消原因
     */
    void cancelOrder(String orderNo, String remark);

    /**
     * 订单发货（顺序消息）
     *
     * <p>使用顺序消息保证同一订单的操作按序处理（如发货、取消不会乱序）</p>
     *
     * @param orderNo 订单编号
     */
    void deliverOrder(String orderNo);
}
