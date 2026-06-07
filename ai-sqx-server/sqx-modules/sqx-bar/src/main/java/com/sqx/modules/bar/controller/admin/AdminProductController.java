package com.sqx.modules.bar.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.bar.domain.bo.ProductQueryBo;
import com.sqx.modules.bar.domain.bo.ProductSaveBo;
import com.sqx.modules.bar.domain.vo.ProductVo;
import com.sqx.modules.bar.service.BarProductService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 酒水商品
 */
@RestController
@RequestMapping("/admin/product")
public class AdminProductController {

    private final BarProductService productService;

    public AdminProductController(BarProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/list")
    @RequiresPermissions("bar:product:list")
    public Result<PageResult<ProductVo>> list(ProductQueryBo queryBo) {
        return Result.ok(productService.page(queryBo));
    }

    @GetMapping("/detail/{id}")
    @RequiresPermissions("bar:product:detail")
    public Result<ProductVo> detail(@PathVariable Long id) {
        return Result.ok(productService.detail(id));
    }

    @PostMapping("/save")
    @RequiresPermissions("bar:product:save")
    public Result<Void> save(@RequestBody ProductSaveBo bo) {
        productService.save(bo);
        return Result.ok();
    }

    @PutMapping("/update")
    @RequiresPermissions("bar:product:update")
    public Result<Void> update(@RequestBody ProductSaveBo bo) {
        productService.update(bo);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    @RequiresPermissions("bar:product:delete")
    public Result<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.ok();
    }
}
