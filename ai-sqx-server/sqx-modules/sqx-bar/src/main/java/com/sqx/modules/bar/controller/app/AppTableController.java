package com.sqx.modules.bar.controller.app;

import com.sqx.common.Result;
import com.sqx.common.annotation.Login;
import com.sqx.modules.bar.domain.vo.TableVo;
import com.sqx.modules.bar.service.BarTableService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 用户端 - 桌位
 */
@RestController
@RequestMapping("/app/table")
public class AppTableController {

    private final BarTableService tableService;

    public AppTableController(BarTableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping("/list")
    @Login
    public Result<List<TableVo>> list() {
        return Result.ok(tableService.listAvailable());
    }
}
