package com.hnhegui.hc.controller.order;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.hnhegui.hc.bo.order.OrderBO;
import com.hnhegui.hc.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单 Controller（MQ 集成版）
 *
 * <p>鉴权说明：</p>
 * <ul>
 *   <li>Gateway 层：Token 校验 + UserContext 透传</li>
 *   <li>服务层：SaInterceptor 默认要求登录，注解控制细粒度权限</li>
 * </ul>
 */
@RestController
@RequestMapping("/order")
@SaCheckLogin
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // ==================== 基础 CRUD ====================

    /**
     * 查询订单列表
     */
    @SaCheckPermission("order:list")
    @GetMapping("/list")
    public List<OrderBO> list() {
        return orderService.listBO();
    }

    /**
     * 根据ID查询订单
     */
    @SaCheckPermission("order:list")
    @GetMapping("/get/{id}")
    public OrderBO getById(@PathVariable Long id) {
        return orderService.getBOById(id);
    }

    /**
     * 根据订单编号查询订单
     */
    @SaCheckPermission("order:list")
    @GetMapping("/getByOrderNo/{orderNo}")
    public OrderBO getByOrderNo(@PathVariable String orderNo) {
        return orderService.getBOByOrderNo(orderNo);
    }

    /**
     * 创建订单（基础版，不含MQ）
     */
    @SaCheckPermission("order:add")
    @PostMapping("/save")
    public Long save(@RequestBody OrderBO bo) {
        return orderService.saveBO(bo);
    }

    /**
     * 更新订单
     */
    @SaCheckPermission("order:edit")
    @PutMapping("/update")
    public void update(@RequestBody OrderBO bo) {
        orderService.updateBO(bo);
    }

    // ==================== MQ 集成接口 ====================

    /**
     * 创建订单（MQ 集成版）
     *
     * <p>创建订单同时发送普通消息和延迟超时检查消息</p>
     *
     * <p>请求示例：</p>
     * <pre>{@code
     * {
     *   "userId": 10001,
     *   "totalAmount": 299.00,
     *   "payAmount": 299.00,
     *   "discountAmount": 0.00,
     *   "remark": "测试订单"
     * }
     * }</pre>
     */
    @SaCheckPermission("order:add")
    @PostMapping("/create")
    public String create(@RequestBody OrderBO bo) {
        return orderService.createOrderWithMQ(bo);
    }

    /**
     * 支付订单（事务消息）
     *
     * <p>请求示例：</p>
     * <pre>{@code
     * {
     *   "orderNo": "ORD1717286400000abc12345",
     *   "payType": "WECHAT",
     *   "payAmount": 299.00
     * }
     * }</pre>
     */
    @SaCheckPermission("order:pay")
    @PostMapping("/pay")
    public String pay(@RequestBody OrderPayRequest request) {
        orderService.payOrder(request.getOrderNo(), request.getPayType(), request.getPayAmount());
        return "支付处理成功: " + request.getOrderNo();
    }

    /**
     * 取消订单（事务消息）
     *
     * <p>请求示例：</p>
     * <pre>{@code
     * {
     *   "orderNo": "ORD1717286400000abc12345",
     *   "remark": "用户主动取消"
     * }
     * }</pre>
     */
    @SaCheckPermission("order:cancel")
    @PostMapping("/cancel")
    public String cancel(@RequestBody OrderCancelRequest request) {
        orderService.cancelOrder(request.getOrderNo(), request.getRemark());
        return "取消处理成功: " + request.getOrderNo();
    }

    /**
     * 订单发货（顺序消息）
     *
     * <p>请求示例：</p>
     * <pre>{@code
     * {
     *   "orderNo": "ORD1717286400000abc12345"
     * }
     * }</pre>
     */
    @SaCheckPermission("order:deliver")
    @PostMapping("/deliver")
    public String deliver(@RequestBody OrderDeliverRequest request) {
        orderService.deliverOrder(request.getOrderNo());
        return "发货处理成功: " + request.getOrderNo();
    }

    // ==================== 内嵌请求类 ====================

    @lombok.Data
    public static class OrderPayRequest {
        private String orderNo;
        private String payType;
        private BigDecimal payAmount;
    }

    @lombok.Data
    public static class OrderCancelRequest {
        private String orderNo;
        private String remark;
    }

    @lombok.Data
    public static class OrderDeliverRequest {
        private String orderNo;
    }
}
