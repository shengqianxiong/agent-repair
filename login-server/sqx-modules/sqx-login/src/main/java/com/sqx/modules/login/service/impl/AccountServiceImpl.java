package com.sqx.modules.login.service.impl;

import cn.hutool.core.bean.BeanUtil;
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
import com.sqx.modules.login.service.AccountService;
import com.sqx.modules.login.service.TokenService;
import com.sqx.modules.login.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 账号业务服务实现
 */
@Service
public class AccountServiceImpl implements AccountService {

    private final LoginAccountMapper accountMapper;
    private final TokenService tokenService;

    public AccountServiceImpl(LoginAccountMapper accountMapper, TokenService tokenService) {
        this.accountMapper = accountMapper;
        this.tokenService = tokenService;
    }

    @Override
    public LoginVo appLogin(LoginBo loginBo) {
        LoginAccount account = authenticate(loginBo);
        return buildLoginVo(account);
    }

    @Override
    public LoginVo adminLogin(LoginBo loginBo) {
        LoginAccount account = authenticate(loginBo);
        if (account.getRole() != AccountConstant.ROLE_ADMIN) {
            throw new BusinessException(403, "无访问权限");
        }
        return buildLoginVo(account);
    }

    @Override
    public PageResult<AccountVo> page(AccountQueryBo queryBo) {
        int page = queryBo.getPage() == null || queryBo.getPage() < 1 ? 1 : queryBo.getPage();
        int pageSize = queryBo.getPageSize() == null || queryBo.getPageSize() < 1 ? 10 : queryBo.getPageSize();

        LambdaQueryWrapper<LoginAccount> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(queryBo.getUsername())) {
            wrapper.like(LoginAccount::getUsername, queryBo.getUsername());
        }
        wrapper.orderByDesc(LoginAccount::getCreateTime);

        Page<LoginAccount> pageData = accountMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<AccountVo> list = pageData.getRecords().stream()
                .map(this::toVo)
                .collect(Collectors.toList());
        return PageResult.of(list, pageData.getTotal(), page, pageSize);
    }

    @Override
    public AccountVo detail(Long id) {
        return toVo(getAccountOrThrow(id));
    }

    @Override
    public void save(AccountSaveBo saveBo) {
        validateSaveBo(saveBo);
        Date now = new Date();
        LoginAccount account = new LoginAccount();
        account.setUsername(saveBo.getUsername().trim());
        account.setPassword(PasswordUtil.encrypt(saveBo.getPassword()));
        account.setRole(AccountConstant.ROLE_USER);
        account.setStatus(AccountConstant.STATUS_NORMAL);
        account.setCreateTime(now);
        account.setUpdateTime(now);
        accountMapper.insert(account);
    }

    @Override
    public void update(AccountUpdateBo updateBo) {
        if (updateBo.getId() == null) {
            throw new BusinessException("账号ID不能为空");
        }
        LoginAccount account = getAccountOrThrow(updateBo.getId());
        if (StrUtil.isNotBlank(updateBo.getPassword())) {
            account.setPassword(PasswordUtil.encrypt(updateBo.getPassword()));
        }
        if (updateBo.getStatus() != null) {
            if (updateBo.getStatus() != AccountConstant.STATUS_NORMAL
                    && updateBo.getStatus() != AccountConstant.STATUS_DISABLED) {
                throw new BusinessException("账号状态不合法");
            }
            account.setStatus(updateBo.getStatus());
        }
        account.setUpdateTime(new Date());
        accountMapper.updateById(account);
    }

    @Override
    public void delete(Long id, Long currentAccountId) {
        if (id == null) {
            throw new BusinessException("账号ID不能为空");
        }
        if (id.equals(currentAccountId)) {
            throw new BusinessException("不可删除当前登录的管理员账号");
        }
        getAccountOrThrow(id);
        accountMapper.deleteById(id);
    }

    @Override
    public LoginAccount getById(Long id) {
        if (id == null) {
            return null;
        }
        return accountMapper.selectById(id);
    }

    private LoginAccount authenticate(LoginBo loginBo) {
        validateLoginBo(loginBo);
        LoginAccount account = accountMapper.selectOne(new LambdaQueryWrapper<LoginAccount>()
                .eq(LoginAccount::getUsername, loginBo.getUsername().trim()));
        if (account == null || !PasswordUtil.matches(loginBo.getPassword(), account.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        if (account.getStatus() == AccountConstant.STATUS_DISABLED) {
            throw new BusinessException("账号已被禁用");
        }
        return account;
    }

    private LoginVo buildLoginVo(LoginAccount account) {
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(tokenService.createToken(account.getId()));
        loginVo.setAccountId(account.getId());
        loginVo.setUsername(account.getUsername());
        return loginVo;
    }

    private void validateLoginBo(LoginBo loginBo) {
        if (loginBo == null) {
            throw new BusinessException("请输入账号/密码");
        }
        if (StrUtil.isBlank(loginBo.getUsername())) {
            throw new BusinessException("请输入账号");
        }
        if (StrUtil.isBlank(loginBo.getPassword())) {
            throw new BusinessException("请输入密码");
        }
    }

    private void validateSaveBo(AccountSaveBo saveBo) {
        if (saveBo == null || StrUtil.isBlank(saveBo.getUsername())) {
            throw new BusinessException("账号不能为空");
        }
        if (StrUtil.isBlank(saveBo.getPassword())) {
            throw new BusinessException("初始密码不能为空");
        }
        Long count = accountMapper.selectCount(new LambdaQueryWrapper<LoginAccount>()
                .eq(LoginAccount::getUsername, saveBo.getUsername().trim()));
        if (count != null && count > 0) {
            throw new BusinessException("账号已存在");
        }
    }

    private LoginAccount getAccountOrThrow(Long id) {
        LoginAccount account = accountMapper.selectById(id);
        if (account == null) {
            throw new BusinessException("账号不存在");
        }
        return account;
    }

    private AccountVo toVo(LoginAccount account) {
        AccountVo vo = new AccountVo();
        BeanUtil.copyProperties(account, vo);
        return vo;
    }
}
