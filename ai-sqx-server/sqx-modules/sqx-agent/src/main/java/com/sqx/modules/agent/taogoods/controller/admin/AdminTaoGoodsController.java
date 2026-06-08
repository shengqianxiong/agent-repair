package com.sqx.modules.agent.taogoods.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.taogoods.db.entity.TaoGoods;
import com.sqx.modules.agent.taogoods.domain.bo.TaoGoodsSaveBo;
import com.sqx.modules.agent.taogoods.service.TaoGoodsService;
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
 * 管理端 - 淘商品（软删除 + 回收站）
 */
@RestController
@RequestMapping("/admin/agent/tao-goods")
public class AdminTaoGoodsController {

    private final TaoGoodsService taoGoodsService;

    public AdminTaoGoodsController(TaoGoodsService taoGoodsService) {
        this.taoGoodsService = taoGoodsService;
    }

    @GetMapping("/list")
    @RequiresPermissions("agent:taoGoods:list")
    public Result<PageResult<TaoGoods>> list(AgentPageQueryBo queryBo) {
        return Result.ok(taoGoodsService.page(queryBo));
    }

    @GetMapping("/detail/{id}")
    @RequiresPermissions("agent:taoGoods:detail")
    public Result<TaoGoods> detail(@PathVariable Long id) {
        return Result.ok(taoGoodsService.detail(id));
    }

    @PostMapping("/save")
    @RequiresPermissions("agent:taoGoods:save")
    public Result<Void> save(@RequestBody TaoGoodsSaveBo bo) {
        taoGoodsService.save(bo);
        return Result.ok();
    }

    @PutMapping("/update")
    @RequiresPermissions("agent:taoGoods:update")
    public Result<Void> update(@RequestBody TaoGoodsSaveBo bo) {
        taoGoodsService.update(bo);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    @RequiresPermissions("agent:taoGoods:delete")
    public Result<Void> delete(@PathVariable Long id) {
        taoGoodsService.delete(id);
        return Result.ok();
    }

    @GetMapping("/recycle/list")
    @RequiresPermissions("agent:taoGoods:recycle")
    public Result<PageResult<TaoGoods>> recycleList(AgentPageQueryBo queryBo) {
        return Result.ok(taoGoodsService.recyclePage(queryBo));
    }

    @PutMapping("/restore/{id}")
    @RequiresPermissions("agent:taoGoods:restore")
    public Result<Void> restore(@PathVariable Long id) {
        taoGoodsService.restore(id);
        return Result.ok();
    }

    @DeleteMapping("/permanent/{id}")
    @RequiresPermissions("agent:taoGoods:permanent")
    public Result<Void> permanentDelete(@PathVariable Long id) {
        taoGoodsService.permanentDelete(id);
        return Result.ok();
    }
}
