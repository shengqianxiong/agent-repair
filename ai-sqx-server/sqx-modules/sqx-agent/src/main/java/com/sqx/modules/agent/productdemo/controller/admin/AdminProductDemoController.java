package com.sqx.modules.agent.productdemo.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.productdemo.db.entity.ProductDemo;
import com.sqx.modules.agent.productdemo.domain.bo.ProductDemoSaveBo;
import com.sqx.modules.agent.productdemo.service.ProductDemoService;
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
 * 管理端 - 产品演示（软删除 + 回收站）
 */
@RestController
@RequestMapping("/admin/agent/product-demo")
public class AdminProductDemoController {

    private final ProductDemoService productDemoService;

    public AdminProductDemoController(ProductDemoService productDemoService) {
        this.productDemoService = productDemoService;
    }

    @GetMapping("/list")
    @RequiresPermissions("agent:productDemo:list")
    public Result<PageResult<ProductDemo>> list(AgentPageQueryBo queryBo) {
        return Result.ok(productDemoService.page(queryBo));
    }

    @GetMapping("/detail/{id}")
    @RequiresPermissions("agent:productDemo:detail")
    public Result<ProductDemo> detail(@PathVariable Long id) {
        return Result.ok(productDemoService.detail(id));
    }

    @PostMapping("/save")
    @RequiresPermissions("agent:productDemo:save")
    public Result<Void> save(@RequestBody ProductDemoSaveBo bo) {
        productDemoService.save(bo);
        return Result.ok();
    }

    @PutMapping("/update")
    @RequiresPermissions("agent:productDemo:update")
    public Result<Void> update(@RequestBody ProductDemoSaveBo bo) {
        productDemoService.update(bo);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    @RequiresPermissions("agent:productDemo:delete")
    public Result<Void> delete(@PathVariable Long id) {
        productDemoService.delete(id);
        return Result.ok();
    }

    @GetMapping("/recycle/list")
    @RequiresPermissions("agent:productDemo:recycle")
    public Result<PageResult<ProductDemo>> recycleList(AgentPageQueryBo queryBo) {
        return Result.ok(productDemoService.recyclePage(queryBo));
    }

    @PutMapping("/restore/{id}")
    @RequiresPermissions("agent:productDemo:restore")
    public Result<Void> restore(@PathVariable Long id) {
        productDemoService.restore(id);
        return Result.ok();
    }

    @DeleteMapping("/permanent/{id}")
    @RequiresPermissions("agent:productDemo:permanent")
    public Result<Void> permanentDelete(@PathVariable Long id) {
        productDemoService.permanentDelete(id);
        return Result.ok();
    }
}
