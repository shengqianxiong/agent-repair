package com.sqx.modules.bar.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.bar.domain.bo.OrderQueryBo;
import com.sqx.modules.bar.domain.bo.OrderStatusBo;
import com.sqx.modules.bar.domain.vo.OrderVo;
import com.sqx.modules.bar.service.BarOrderService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 订单管理
 */
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    private final BarOrderService orderService;

    public AdminOrderController(BarOrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/list")
    @RequiresPermissions("bar:order:list")
    public Result<PageResult<OrderVo>> list(OrderQueryBo queryBo) {
        return Result.ok(orderService.page(queryBo));
    }

    @GetMapping("/detail/{id}")
    @RequiresPermissions("bar:order:detail")
    public Result<OrderVo> detail(@PathVariable Long id) {
        return Result.ok(orderService.detail(id));
    }

    @PutMapping("/status")
    @RequiresPermissions("bar:order:update")
    public Result<Void> updateStatus(@RequestBody OrderStatusBo bo) {
        orderService.updateStatus(bo);
        return Result.ok();
    }

    @PostMapping("/refund/{id}")
    @RequiresPermissions("bar:order:refund")
    public Result<Void> refund(@PathVariable Long id) {
        orderService.refund(id);
        return Result.ok();
    }
}
