package com.sqx.modules.login.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.login.db.entity.LoginAccount;
import com.sqx.modules.login.db.mapper.LoginAccountMapper;
import com.sqx.modules.login.domain.bo.AccountLoginBo;
import com.sqx.modules.login.domain.bo.AccountQueryBo;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.vo.AccountLoginVo;
import com.sqx.modules.login.domain.vo.AccountVo;
import com.sqx.modules.login.service.LoginAccountService;
import com.sqx.modules.login.service.TokenService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录账号业务实现
 */
@Service
public class LoginAccountServiceImpl implements LoginAccountService {

    /** 账号状态：禁用 */
    private static final int STATUS_DISABLED = 0;
    /** 账号状态：正常 */
    private static final int STATUS_NORMAL = 1;

    private final LoginAccountMapper accountMapper;
    private final TokenService tokenService;

    public LoginAccountServiceImpl(LoginAccountMapper accountMapper, TokenService tokenService) {
        this.accountMapper = accountMapper;
        this.tokenService = tokenService;
    }

    @Override
    public AccountLoginVo login(AccountLoginBo loginBo) {
        if (loginBo == null || StrUtil.isBlank(loginBo.getUsername())) {
            throw new BusinessException("请输入账号");
        }
        if (StrUtil.isBlank(loginBo.getPassword())) {
            throw new BusinessException("请输入密码");
        }

        LoginAccount account = findByUsername(loginBo.getUsername());
        if (account == null) {
            throw new BusinessException("账号或密码错误");
        }
        if (STATUS_DISABLED == account.getStatus()) {
            throw new BusinessException("账号已被禁用");
        }
        String encryptedPassword = encryptPassword(loginBo.getPassword());
        if (!encryptedPassword.equals(account.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }

        AccountLoginVo loginVo = new AccountLoginVo();
        loginVo.setAccountId(account.getId());
        loginVo.setUsername(account.getUsername());
        loginVo.setToken(tokenService.createToken(account.getId()));
        return loginVo;
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
        LoginAccount account = getAccountOrThrow(id);
        return toVo(account);
    }

    @Override
    public void save(AccountSaveBo saveBo) {
        if (saveBo == null || StrUtil.isBlank(saveBo.getUsername())) {
            throw new BusinessException("请输入账号");
        }
        if (StrUtil.isBlank(saveBo.getPassword())) {
            throw new BusinessException("请输入初始密码");
        }
        if (findByUsername(saveBo.getUsername()) != null) {
            throw new BusinessException("账号已存在");
        }

        Date now = new Date();
        LoginAccount account = new LoginAccount();
        account.setUsername(saveBo.getUsername().trim());
        account.setPassword(encryptPassword(saveBo.getPassword()));
        account.setStatus(STATUS_NORMAL);
        account.setCreateTime(now);
        account.setUpdateTime(now);
        accountMapper.insert(account);
    }

    @Override
    public void update(AccountUpdateBo updateBo) {
        if (updateBo == null || updateBo.getId() == null) {
            throw new BusinessException("账号ID不能为空");
        }
        LoginAccount account = getAccountOrThrow(updateBo.getId());

        if (StrUtil.isNotBlank(updateBo.getPassword())) {
            account.setPassword(encryptPassword(updateBo.getPassword()));
        }
        if (updateBo.getStatus() != null) {
            if (updateBo.getStatus() != STATUS_DISABLED && updateBo.getStatus() != STATUS_NORMAL) {
                throw new BusinessException("账号状态不合法");
            }
            account.setStatus(updateBo.getStatus());
        }
        account.setUpdateTime(new Date());
        accountMapper.updateById(account);
    }

    @Override
    public void delete(Long id, Long currentAccountId) {
        LoginAccount account = getAccountOrThrow(id);
        if (currentAccountId != null && currentAccountId.equals(account.getId())) {
            throw new BusinessException("不可删除当前登录的管理员账号");
        }
        accountMapper.deleteById(id);
    }

    private LoginAccount findByUsername(String username) {
        LambdaQueryWrapper<LoginAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginAccount::getUsername, username.trim());
        return accountMapper.selectOne(wrapper);
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

    private String encryptPassword(String rawPassword) {
        return DigestUtil.md5Hex(rawPassword);
    }
}
