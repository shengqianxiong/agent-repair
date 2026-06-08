package com.sqx.modules.agent.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.agent.domain.bo.AgentDeliverySaveBo;
import com.sqx.modules.agent.domain.bo.AgentPageQueryBo;
import com.sqx.modules.agent.domain.vo.AgentDeliveryVo;
import com.sqx.modules.agent.service.AgentDeliveryService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 客户交付（含软删除与回收站）
 */
@RestController
@RequestMapping("/admin/agent/delivery")
public class AdminAgentDeliveryController {

    private final AgentDeliveryService deliveryService;

    public AdminAgentDeliveryController(AgentDeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/list")
    public Result<PageResult<AgentDeliveryVo>> list(AgentPageQueryBo queryBo) {
        return Result.ok(deliveryService.page(queryBo));
    }

    @GetMapping("/detail/{id}")
    public Result<AgentDeliveryVo> detail(@PathVariable Long id) {
        return Result.ok(deliveryService.detail(id));
    }

    @PostMapping("/save")
    public Result<Void> save(@RequestBody AgentDeliverySaveBo bo) {
        deliveryService.save(bo);
        return Result.ok();
    }

    @PutMapping("/update")
    public Result<Void> update(@RequestBody AgentDeliverySaveBo bo) {
        deliveryService.update(bo);
        return Result.ok();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        deliveryService.softDelete(id);
        return Result.ok();
    }

    @GetMapping("/recycle/list")
    public Result<PageResult<AgentDeliveryVo>> recycleList(AgentPageQueryBo queryBo) {
        return Result.ok(deliveryService.recyclePage(queryBo));
    }

    @PutMapping("/restore/{id}")
    public Result<Void> restore(@PathVariable Long id) {
        deliveryService.restore(id);
        return Result.ok();
    }

    @DeleteMapping("/permanent/{id}")
    public Result<Void> permanentDelete(@PathVariable Long id) {
        deliveryService.permanentDelete(id);
        return Result.ok();
    }
}
