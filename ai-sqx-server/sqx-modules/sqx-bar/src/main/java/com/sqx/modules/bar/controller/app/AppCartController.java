package com.sqx.modules.bar.controller.app;

import com.sqx.common.Result;
import com.sqx.common.annotation.Login;
import com.sqx.common.utils.UserContext;
import com.sqx.modules.bar.domain.bo.CartAddBo;
import com.sqx.modules.bar.domain.bo.CartUpdateBo;
import com.sqx.modules.bar.domain.vo.CartVo;
import com.sqx.modules.bar.service.BarCartService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端 - 购物车
 */
@RestController
@RequestMapping("/app/cart")
public class AppCartController {

    private final BarCartService cartService;

    public AppCartController(BarCartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/list")
    @Login
    public Result<List<CartVo>> list() {
        return Result.ok(cartService.list(UserContext.getUserId()));
    }

    @PostMapping("/add")
    @Login
    public Result<Void> add(@RequestBody CartAddBo bo) {
        cartService.add(UserContext.getUserId(), bo);
        return Result.ok();
    }

    @PutMapping("/update")
    @Login
    public Result<Void> update(@RequestBody CartUpdateBo bo) {
        cartService.update(UserContext.getUserId(), bo);
        return Result.ok();
    }

    @DeleteMapping("/remove/{id}")
    @Login
    public Result<Void> remove(@PathVariable Long id) {
        cartService.remove(UserContext.getUserId(), id);
        return Result.ok();
    }
}
