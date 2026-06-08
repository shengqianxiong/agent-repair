package com.sqx.modules.agent.userlink.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.userlink.db.entity.UserLink;
import com.sqx.modules.agent.userlink.domain.bo.UserLinkQueryBo;
import com.sqx.modules.agent.userlink.domain.bo.UserLinkSaveBo;
import com.sqx.modules.agent.userlink.service.UserLinkService;
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
 * 管理端 - 用户链接（软删除 + 回收站）
 */
@RestController
@RequestMapping("/admin/agent/user-link")
public class AdminUserLinkController {

    private final UserLinkService userLinkService;

    public AdminUserLinkController(UserLinkService userLinkService) {
        this.userLinkService = userLinkService;
    }

    @GetMapping("/list")
    @RequiresPermissions("agent:userLink:list")
    public Result<PageResult<UserLink>> list(UserLinkQueryBo queryBo) {
        return Result.ok(userLinkService.page(queryBo));
    }

    @GetMapping("/detail/{id}")
    @RequiresPermissions("agent:userLink:detail")
    public Result<UserLink> detail(@PathVariable Long id) {
        return Result.ok(userLinkService.detail(id));
    }

    @PostMapping("/save")
    @RequiresPermissions("agent:userLink:save")
    public Result<Void> save(@RequestBody UserLinkSaveBo bo) {
        userLinkService.save(bo);
        return Result.ok();
    }

    @PutMapping("/update")
    @RequiresPermissions("agent:userLink:update")
    public Result<Void> update(@RequestBody UserLinkSaveBo bo) {
        userLinkService.update(bo);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    @RequiresPermissions("agent:userLink:delete")
    public Result<Void> delete(@PathVariable Long id) {
        userLinkService.delete(id);
        return Result.ok();
    }

    @GetMapping("/recycle/list")
    @RequiresPermissions("agent:userLink:recycle")
    public Result<PageResult<UserLink>> recycleList(AgentPageQueryBo queryBo) {
        return Result.ok(userLinkService.recyclePage(queryBo));
    }

    @PutMapping("/restore/{id}")
    @RequiresPermissions("agent:userLink:restore")
    public Result<Void> restore(@PathVariable Long id) {
        userLinkService.restore(id);
        return Result.ok();
    }

    @DeleteMapping("/permanent/{id}")
    @RequiresPermissions("agent:userLink:permanent")
    public Result<Void> permanentDelete(@PathVariable Long id) {
        userLinkService.permanentDelete(id);
        return Result.ok();
    }
}
