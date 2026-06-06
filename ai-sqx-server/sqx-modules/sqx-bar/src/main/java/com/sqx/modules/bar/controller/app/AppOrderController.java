package com.sqx.modules.bar.controller.app;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.common.annotation.Login;
import com.sqx.common.utils.UserContext;
import com.sqx.modules.bar.domain.bo.OrderPayBo;
import com.sqx.modules.bar.domain.bo.OrderQueryBo;
import com.sqx.modules.bar.domain.bo.OrderSubmitBo;
import com.sqx.modules.bar.domain.vo.OrderVo;
import com.sqx.modules.bar.domain.vo.PayVo;
import com.sqx.modules.bar.service.BarOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端 - 订单
 */
@RestController
@RequestMapping("/app/order")
public class AppOrderController {

    private final BarOrderService orderService;

    public AppOrderController(BarOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/submit")
    @Login
    public Result<OrderVo> submit(@RequestBody OrderSubmitBo bo) {
        return Result.ok(orderService.submit(UserContext.getUserId(), bo));
    }

    @PostMapping("/pay")
    @Login
    public Result<PayVo> pay(@RequestBody OrderPayBo bo) {
        PayVo payVo = orderService.pay(UserContext.getUserId(), bo);
        orderService.confirmPay(bo.getOrderId());
        return Result.ok(payVo);
    }

    @GetMapping("/list")
    @Login
    public Result<PageResult<OrderVo>> list(OrderQueryBo queryBo) {
        queryBo.setUserId(UserContext.getUserId());
        return Result.ok(orderService.page(queryBo));
    }

    @GetMapping("/detail/{id}")
    @Login
    public Result<OrderVo> detail(@PathVariable Long id) {
        OrderVo orderVo = orderService.detail(id);
        if (!orderVo.getUserId().equals(UserContext.getUserId())) {
            return Result.error("无权查看该订单");
        }
        return Result.ok(orderVo);
    }

    @PutMapping("/cancel/{id}")
    @Login
    public Result<Void> cancel(@PathVariable Long id) {
        orderService.cancel(UserContext.getUserId(), id);
        return Result.ok();
    }
}
