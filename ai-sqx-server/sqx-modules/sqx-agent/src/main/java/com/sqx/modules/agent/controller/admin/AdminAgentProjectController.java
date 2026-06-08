package com.sqx.modules.agent.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.agent.domain.bo.AgentPageQueryBo;
import com.sqx.modules.agent.domain.bo.AgentProjectSaveBo;
import com.sqx.modules.agent.domain.vo.AgentProjectVo;
import com.sqx.modules.agent.service.AgentProjectService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 自动化项目（含软删除与回收站）
 */
@RestController
@RequestMapping("/admin/agent/project")
public class AdminAgentProjectController {

    private final AgentProjectService projectService;

    public AdminAgentProjectController(AgentProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/list")
    public Result<PageResult<AgentProjectVo>> list(AgentPageQueryBo queryBo) {
        return Result.ok(projectService.page(queryBo));
    }

    @GetMapping("/detail/{id}")
    public Result<AgentProjectVo> detail(@PathVariable Long id) {
        return Result.ok(projectService.detail(id));
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody AgentProjectSaveBo bo) {
        projectService.save(bo);
        return Result.ok();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody AgentProjectSaveBo bo) {
        projectService.update(bo);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        projectService.softDelete(id);
        return Result.ok();
    }

    @GetMapping("/recycle/list")
    public Result<PageResult<AgentProjectVo>> recycleList(AgentPageQueryBo queryBo) {
        return Result.ok(projectService.recyclePage(queryBo));
    }

    @PutMapping("/restore/{id}")
    public Result<Void> restore(@PathVariable Long id) {
        projectService.restore(id);
        return Result.ok();
    }

    @DeleteMapping("/permanent/{id}")
    public Result<Void> permanentDelete(@PathVariable Long id) {
        projectService.permanentDelete(id);
        return Result.ok();
    }
}
