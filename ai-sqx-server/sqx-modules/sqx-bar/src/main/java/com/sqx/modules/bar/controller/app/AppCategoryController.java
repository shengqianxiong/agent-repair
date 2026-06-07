package com.sqx.modules.bar.controller.app;

import com.sqx.common.Result;
import com.sqx.common.annotation.Login;
import com.sqx.modules.bar.domain.vo.ProductCategoryVo;
import com.sqx.modules.bar.service.BarProductCategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端 - 点餐分类
 */
@RestController
@RequestMapping("/app/category")
public class AppCategoryController {

    private final BarProductCategoryService categoryService;

    public AppCategoryController(BarProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/list")
    @Login
    public Result<List<ProductCategoryVo>> list() {
        return Result.ok(categoryService.listEnabled());
    }
}
