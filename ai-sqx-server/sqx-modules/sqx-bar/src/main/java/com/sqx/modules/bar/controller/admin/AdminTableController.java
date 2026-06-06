package com.sqx.modules.bar.controller.admin;

import com.sqx.common.Result;
import com.sqx.modules.bar.domain.bo.TableSaveBo;
import com.sqx.modules.bar.domain.bo.TableStatusBo;
import com.sqx.modules.bar.domain.vo.TableVo;
import com.sqx.modules.bar.service.BarTableService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 管理端 - 桌位管理
 */
@RestController
@RequestMapping("/admin/table")
public class AdminTableController {

    private final BarTableService tableService;

    public AdminTableController(BarTableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping("/list")
    @RequiresPermissions("bar:table:list")
    public Result<List<TableVo>> list() {
        return Result.ok(tableService.listAll());
    }

    @PostMapping("/save")
    @RequiresPermissions("bar:table:save")
    public Result<Void> save(@RequestBody TableSaveBo bo) {
        tableService.save(bo);
        return Result.ok();
    }

    @PutMapping("/update")
    @RequiresPermissions("bar:table:update")
    public Result<Void> update(@RequestBody TableSaveBo bo) {
        tableService.update(bo);
        return Result.ok();
    }

    @PutMapping("/status")
    @RequiresPermissions("bar:table:update")
    public Result<Void> updateStatus(@RequestBody TableStatusBo bo) {
        tableService.updateStatus(bo);
        return Result.ok();
    }
}
