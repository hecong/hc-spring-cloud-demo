package com.hnhegui.hc.controller.order;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import com.hnhegui.hc.bo.order.OrderBO;
import com.hnhegui.hc.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单 Controller
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
     * 创建订单
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
}
