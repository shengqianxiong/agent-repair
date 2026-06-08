package com.sqx.modules.agent.employee.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.agent.base.AgentPageQueryBo;
import com.sqx.modules.agent.employee.db.entity.AgentEmployee;
import com.sqx.modules.agent.employee.domain.bo.EmployeeSaveBo;
import com.sqx.modules.agent.employee.service.AgentEmployeeService;
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
 * 管理端 - 员工（软删除 + 回收站）
 */
@RestController
@RequestMapping("/admin/agent/employee")
public class AdminAgentEmployeeController {

    private final AgentEmployeeService employeeService;

    public AdminAgentEmployeeController(AgentEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/list")
    @RequiresPermissions("agent:employee:list")
    public Result<PageResult<AgentEmployee>> list(AgentPageQueryBo queryBo) {
        return Result.ok(employeeService.page(queryBo));
    }

    @GetMapping("/detail/{id}")
    @RequiresPermissions("agent:employee:detail")
    public Result<AgentEmployee> detail(@PathVariable Long id) {
        return Result.ok(employeeService.detail(id));
    }

    @PostMapping("/save")
    @RequiresPermissions("agent:employee:save")
    public Result<Void> save(@RequestBody EmployeeSaveBo bo) {
        employeeService.save(bo);
        return Result.ok();
    }

    @PutMapping("/update")
    @RequiresPermissions("agent:employee:update")
    public Result<Void> update(@RequestBody EmployeeSaveBo bo) {
        employeeService.update(bo);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    @RequiresPermissions("agent:employee:delete")
    public Result<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return Result.ok();
    }

    @GetMapping("/recycle/list")
    @RequiresPermissions("agent:employee:recycle")
    public Result<PageResult<AgentEmployee>> recycleList(AgentPageQueryBo queryBo) {
        return Result.ok(employeeService.recyclePage(queryBo));
    }

    @PutMapping("/restore/{id}")
    @RequiresPermissions("agent:employee:restore")
    public Result<Void> restore(@PathVariable Long id) {
        employeeService.restore(id);
        return Result.ok();
    }

    @DeleteMapping("/permanent/{id}")
    @RequiresPermissions("agent:employee:permanent")
    public Result<Void> permanentDelete(@PathVariable Long id) {
        employeeService.permanentDelete(id);
        return Result.ok();
    }
}
