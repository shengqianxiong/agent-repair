package com.sqx.modules.login.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.login.domain.bo.AccountQueryBo;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.vo.AccountVo;
import com.sqx.modules.login.service.LoginAccountService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 账号增删改查
 */
@RestController
@RequestMapping("/admin/account")
public class AdminAccountController {

    private final LoginAccountService accountService;

    public AdminAccountController(LoginAccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * 账号列表，支持按账号关键词模糊查询
     */
    @GetMapping("/list")
    public Result<PageResult<AccountVo>> list(AccountQueryBo queryBo) {
        return Result.ok(accountService.page(queryBo));
    }

    /**
     * 账号详情
     */
    @GetMapping("/detail/{id}")
    public Result<AccountVo> detail(@PathVariable Long id) {
        return Result.ok(accountService.detail(id));
    }

    /**
     * 新增账号，账号唯一且默认状态为正常
     */
    @PostMapping("/save")
    public Result<Void> save(@RequestBody AccountSaveBo saveBo) {
        accountService.save(saveBo);
        return Result.ok();
    }

    /**
     * 编辑账号，可重置密码或修改状态，不可修改账号名称
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody AccountUpdateBo updateBo) {
        accountService.update(updateBo);
        return Result.ok();
    }

    /**
     * 删除账号，不可删除当前登录的管理员账号
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        accountService.delete(id);
        return Result.ok();
    }
}
