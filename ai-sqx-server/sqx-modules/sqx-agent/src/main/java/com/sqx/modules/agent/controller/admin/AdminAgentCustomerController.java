package com.sqx.modules.agent.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.agent.domain.bo.AgentCustomerSaveBo;
import com.sqx.modules.agent.domain.bo.AgentPageQueryBo;
import com.sqx.modules.agent.domain.vo.AgentCustomerVo;
import com.sqx.modules.agent.service.AgentCustomerService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 项目客户（含软删除与回收站）
 */
@RestController
@RequestMapping("/admin/agent/customer")
public class AdminAgentCustomerController {

    private final AgentCustomerService customerService;

    public AdminAgentCustomerController(AgentCustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/list")
    public Result<PageResult<AgentCustomerVo>> list(AgentPageQueryBo queryBo) {
        return Result.ok(customerService.page(queryBo));
    }

    @GetMapping("/detail/{id}")
    public Result<AgentCustomerVo> detail(@PathVariable Long id) {
        return Result.ok(customerService.detail(id));
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody AgentCustomerSaveBo bo) {
        customerService.save(bo);
        return Result.ok();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody AgentCustomerSaveBo bo) {
        customerService.update(bo);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        customerService.softDelete(id);
        return Result.ok();
    }

    @GetMapping("/recycle/list")
    public Result<PageResult<AgentCustomerVo>> recycleList(AgentPageQueryBo queryBo) {
        return Result.ok(customerService.recyclePage(queryBo));
    }

    @PutMapping("/restore/{id}")
    public Result<Void> restore(@PathVariable Long id) {
        customerService.restore(id);
        return Result.ok();
    }

    @DeleteMapping("/permanent/{id}")
    public Result<Void> permanentDelete(@PathVariable Long id) {
        customerService.permanentDelete(id);
        return Result.ok();
    }
}
