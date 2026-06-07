package com.sqx.modules.login.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.PageResult;
import com.sqx.common.constant.AccountConstant;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.login.db.entity.LoginAccount;
import com.sqx.modules.login.db.mapper.LoginAccountMapper;
import com.sqx.modules.login.domain.bo.AccountQueryBo;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.bo.LoginBo;
import com.sqx.modules.login.domain.vo.AccountVo;
import com.sqx.modules.login.domain.vo.LoginVo;
import com.sqx.modules.login.domain.vo.UserInfoVo;
import com.sqx.modules.login.service.AccountService;
import com.sqx.modules.login.service.TokenService;
import com.sqx.modules.login.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 账号业务服务实现
 */
@Service
public class AccountServiceImpl implements AccountService {

    private final LoginAccountMapper loginAccountMapper;
    private final TokenService tokenService;

    public AccountServiceImpl(LoginAccountMapper loginAccountMapper, TokenService tokenService) {
        this.loginAccountMapper = loginAccountMapper;
        this.tokenService = tokenService;
    }

    @Override
    public LoginVo login(LoginBo loginBo, boolean requireAdmin) {
        validateLoginInput(loginBo);
        LoginAccount account = loginAccountMapper.selectOne(new LambdaQueryWrapper<LoginAccount>()
                .eq(LoginAccount::getAccount, loginBo.getUsername().trim()));
        if (account == null || !PasswordUtil.matches(loginBo.getPassword(), account.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        if (AccountConstant.STATUS_DISABLED == account.getStatus()) {
            throw new BusinessException("账号已被禁用");
        }
        if (requireAdmin && AccountConstant.ROLE_ADMIN != account.getRole()) {
            throw new BusinessException(403, "权限不足");
        }
        String token = tokenService.createToken(account.getId());
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        loginVo.setId(account.getId());
        loginVo.setUsername(account.getAccount());
        return loginVo;
    }

    @Override
    public UserInfoVo currentUserInfo(Long userId) {
        LoginAccount account = getAccountOrThrow(userId);
        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setId(account.getId());
        userInfoVo.setUsername(account.getAccount());
        return userInfoVo;
    }

    @Override
    public PageResult<AccountVo> page(AccountQueryBo queryBo) {
        int page = queryBo.getPage() == null || queryBo.getPage() < 1 ? 1 : queryBo.getPage();
        int pageSize = queryBo.getPageSize() == null || queryBo.getPageSize() < 1 ? 10 : queryBo.getPageSize();
        LambdaQueryWrapper<LoginAccount> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(queryBo.getAccount())) {
            wrapper.like(LoginAccount::getAccount, queryBo.getAccount().trim());
        }
        wrapper.orderByDesc(LoginAccount::getCreateTime);
        Page<LoginAccount> pageResult = loginAccountMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<AccountVo> list = pageResult.getRecords().stream()
                .map(this::toAccountVo)
                .collect(Collectors.toList());
        return PageResult.of(list, pageResult.getTotal(), page, pageSize);
    }

    @Override
    public AccountVo detail(Long id) {
        LoginAccount account = getAccountOrThrow(id);
        return toAccountVo(account);
    }

    @Override
    public void save(AccountSaveBo saveBo) {
        if (StrUtil.isBlank(saveBo.getAccount())) {
            throw new BusinessException("请输入账号");
        }
        if (StrUtil.isBlank(saveBo.getPassword())) {
            throw new BusinessException("请输入密码");
        }
        Long count = loginAccountMapper.selectCount(new LambdaQueryWrapper<LoginAccount>()
                .eq(LoginAccount::getAccount, saveBo.getAccount().trim()));
        if (count != null && count > 0) {
            throw new BusinessException("账号已存在");
        }
        LoginAccount account = new LoginAccount();
        account.setAccount(saveBo.getAccount().trim());
        account.setPassword(PasswordUtil.encrypt(saveBo.getPassword()));
        account.setStatus(AccountConstant.STATUS_NORMAL);
        account.setRole(AccountConstant.ROLE_USER);
        loginAccountMapper.insert(account);
    }

    @Override
    public void update(AccountUpdateBo updateBo) {
        LoginAccount account = getAccountOrThrow(updateBo.getId());
        if (StrUtil.isNotBlank(updateBo.getPassword())) {
            account.setPassword(PasswordUtil.encrypt(updateBo.getPassword()));
        }
        if (updateBo.getStatus() != null) {
            account.setStatus(updateBo.getStatus());
        }
        loginAccountMapper.updateById(account);
    }

    @Override
    public void delete(Long id, Long currentAccountId) {
        if (id.equals(currentAccountId)) {
            throw new BusinessException("不可删除当前登录的管理员账号");
        }
        getAccountOrThrow(id);
        loginAccountMapper.deleteById(id);
    }

    private void validateLoginInput(LoginBo loginBo) {
        if (loginBo == null || StrUtil.isBlank(loginBo.getUsername())) {
            throw new BusinessException("请输入用户名");
        }
        if (StrUtil.isBlank(loginBo.getPassword())) {
            throw new BusinessException("请输入密码");
        }
    }

    private LoginAccount getAccountOrThrow(Long id) {
        LoginAccount account = loginAccountMapper.selectById(id);
        if (account == null) {
            throw new BusinessException("账号不存在");
        }
        return account;
    }

    private AccountVo toAccountVo(LoginAccount account) {
        AccountVo vo = new AccountVo();
        vo.setId(account.getId());
        vo.setAccount(account.getAccount());
        vo.setStatus(account.getStatus());
        vo.setStatusText(account.getStatus() == AccountConstant.STATUS_NORMAL ? "正常" : "禁用");
        vo.setCreateTime(account.getCreateTime());
        return vo;
    }
}
