package com.sqx.modules.bar.controller.admin;

import com.sqx.common.Result;
import com.sqx.modules.bar.domain.bo.CategorySaveBo;
import com.sqx.modules.bar.domain.vo.ProductCategoryVo;
import com.sqx.modules.bar.service.BarProductCategoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端 - 酒水分类
 */
@RestController
@RequestMapping("/admin/category")
public class AdminProductCategoryController {

    private final BarProductCategoryService categoryService;

    public AdminProductCategoryController(BarProductCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/list")
    @RequiresPermissions("bar:category:list")
    public Result<List<ProductCategoryVo>> list() {
        return Result.ok(categoryService.listAll());
    }

    @PostMapping("/save")
    @RequiresPermissions("bar:category:save")
    public Result<Void> save(@RequestBody CategorySaveBo bo) {
        categoryService.save(bo);
        return Result.ok();
    }
}
