package com.sqx.modules.login.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.common.annotation.AdminLogin;
import com.sqx.common.utils.UserContext;
import com.sqx.modules.login.domain.bo.AccountQueryBo;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.vo.AccountVo;
import com.sqx.modules.login.service.AccountService;
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
 * 管理端 - 账号管理
 */
@RestController
@RequestMapping("/admin/account")
@AdminLogin
public class AdminAccountController {

    private final AccountService accountService;

    public AdminAccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 账号列表（支持关键词搜索与分页）
     */
    @GetMapping("/list")
    @RequiresPermissions("login:account:list")
    public Result<PageResult<AccountVo>> list(AccountQueryBo queryBo) {
        return Result.ok(accountService.page(queryBo));
    }

    /**
     * 账号详情
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("login:account:detail")
    public Result<AccountVo> detail(@PathVariable Long id) {
        return Result.ok(accountService.detail(id));
    }

    /**
     * 新增账号
     */
    @PostMapping("/save")
    @RequiresPermissions("login:account:save")
    public Result<Void> save(@RequestBody AccountSaveBo saveBo) {
        accountService.save(saveBo);
        return Result.ok();
    }

    /**
     * 编辑账号（重置密码/修改状态）
     */
    @PutMapping("/update")
    @RequiresPermissions("login:account:update")
    public Result<Void> update(@RequestBody AccountUpdateBo updateBo) {
        accountService.update(updateBo);
        return Result.ok();
    }

    /**
     * 删除账号（不可删除当前登录管理员）
     */
    @DeleteMapping("/delete/{id}")
    @RequiresPermissions("login:account:delete")
    public Result<Void> delete(@PathVariable Long id) {
        accountService.delete(id, UserContext.getUserId());
        return Result.ok();
    }
}
