package com.sqx.modules.agent.category.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.category.db.entity.AgentCategory;
import com.sqx.modules.agent.category.domain.bo.CategorySaveBo;
import com.sqx.modules.agent.category.service.AgentCategoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
 * 管理端 - 商品分类（软删除 + 回收站）
 */
@RestController
@RequestMapping("/admin/agent/category")
public class AdminAgentCategoryController {

    private final AgentCategoryService categoryService;

    public AdminAgentCategoryController(AgentCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/list")
    @RequiresPermissions("agent:category:list")
    public Result<PageResult<AgentCategory>> list(AgentPageQueryBo queryBo) {
        return Result.ok(categoryService.page(queryBo));
    }

    @GetMapping("/all")
    @RequiresPermissions("agent:category:list")
    public Result<List<AgentCategory>> all() {
        return Result.ok(categoryService.listAll());
    }

    @GetMapping("/detail/{id}")
    @RequiresPermissions("agent:category:detail")
    public Result<AgentCategory> detail(@PathVariable Long id) {
        return Result.ok(categoryService.detail(id));
    }

    @PostMapping("/save")
    @RequiresPermissions("agent:category:save")
    public Result<Void> save(@RequestBody CategorySaveBo bo) {
        categoryService.save(bo);
        return Result.ok();
    }

    @PutMapping("/update")
    @RequiresPermissions("agent:category:update")
    public Result<Void> update(@RequestBody CategorySaveBo bo) {
        categoryService.update(bo);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    @RequiresPermissions("agent:category:delete")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.ok();
    }

    @GetMapping("/recycle/list")
    @RequiresPermissions("agent:category:recycle")
    public Result<PageResult<AgentCategory>> recycleList(AgentPageQueryBo queryBo) {
        return Result.ok(categoryService.recyclePage(queryBo));
    }

    @PutMapping("/restore/{id}")
    @RequiresPermissions("agent:category:restore")
    public Result<Void> restore(@PathVariable Long id) {
        categoryService.restore(id);
        return Result.ok();
    }

    @DeleteMapping("/permanent/{id}")
    @RequiresPermissions("agent:category:permanent")
    public Result<Void> permanentDelete(@PathVariable Long id) {
        categoryService.permanentDelete(id);
        return Result.ok();
    }
}
