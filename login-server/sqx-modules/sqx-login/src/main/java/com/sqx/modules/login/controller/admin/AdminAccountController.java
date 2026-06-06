package com.sqx.modules.login.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.common.utils.AdminContext;
import com.sqx.modules.login.domain.bo.AccountLoginBo;
import com.sqx.modules.login.domain.bo.AccountQueryBo;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.vo.AccountLoginVo;
import com.sqx.modules.login.domain.vo.AccountVo;
import com.sqx.modules.login.service.LoginAccountService;
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
public class AdminAccountController {

    private final LoginAccountService accountService;

    public AdminAccountController(LoginAccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Result<AccountLoginVo> login(@RequestBody AccountLoginBo loginBo) {
        return Result.ok(accountService.login(loginBo));
    }

    /**
     * 账号列表（支持关键词模糊查询）
     */
    @GetMapping("/list")
    @RequiresPermissions("account:list")
    public Result<PageResult<AccountVo>> list(AccountQueryBo queryBo) {
        return Result.ok(accountService.page(queryBo));
    }

    /**
     * 账号详情
     */
    @GetMapping("/detail/{id}")
    @RequiresPermissions("account:detail")
    public Result<AccountVo> detail(@PathVariable Long id) {
        return Result.ok(accountService.detail(id));
    }

    /**
     * 新增账号
     */
    @PostMapping("/save")
    @RequiresPermissions("account:save")
    public Result<Void> save(@RequestBody AccountSaveBo saveBo) {
        accountService.save(saveBo);
        return Result.ok();
    }

    /**
     * 编辑账号（重置密码、修改状态）
     */
    @PutMapping("/update")
    @RequiresPermissions("account:update")
    public Result<Void> update(@RequestBody AccountUpdateBo updateBo) {
        accountService.update(updateBo);
        return Result.ok();
    }

    /**
     * 删除账号
     */
    @DeleteMapping("/delete/{id}")
    @RequiresPermissions("account:delete")
    public Result<Void> delete(@PathVariable Long id) {
        accountService.delete(id, AdminContext.getAccountId());
        return Result.ok();
    }
}
