package com.sqx.modules.login.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.PageResult;
import com.sqx.common.constants.AccountConstants;
import com.sqx.common.exception.BusinessException;
import com.sqx.common.utils.PasswordUtils;
import com.sqx.framework.token.TokenService;
import com.sqx.modules.login.db.entity.LoginAccount;
import com.sqx.modules.login.db.mapper.LoginAccountMapper;
import com.sqx.modules.login.domain.bo.AccountLoginBo;
import com.sqx.modules.login.domain.bo.AccountQueryBo;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.vo.AccountLoginVo;
import com.sqx.modules.login.domain.vo.AccountVo;
import com.sqx.modules.login.service.LoginAccountService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录账号业务服务实现。
 */
@Service
public class LoginAccountServiceImpl implements LoginAccountService {

    private final LoginAccountMapper accountMapper;
    private final TokenService tokenService;

    public LoginAccountServiceImpl(LoginAccountMapper accountMapper, TokenService tokenService) {
        this.accountMapper = accountMapper;
        this.tokenService = tokenService;
    }

    @Override
    public AccountLoginVo appLogin(AccountLoginBo loginBo) {
        LoginAccount account = authenticate(loginBo);
        return buildLoginVo(account);
    }

    @Override
    public AccountLoginVo adminLogin(AccountLoginBo loginBo) {
        LoginAccount account = authenticate(loginBo);
        if (account.getRole() == null || account.getRole() != AccountConstants.ROLE_ADMIN) {
            throw new BusinessException(403, "权限不足");
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
        if (existsByUsername(saveBo.getUsername())) {
            throw new BusinessException("账号已存在");
        }
        Date now = new Date();
        LoginAccount account = new LoginAccount();
        account.setUsername(saveBo.getUsername().trim());
        account.setPassword(PasswordUtils.encrypt(saveBo.getPassword()));
        account.setStatus(AccountConstants.STATUS_NORMAL);
        account.setRole(AccountConstants.ROLE_USER);
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
            account.setPassword(PasswordUtils.encrypt(updateBo.getPassword()));
        }
        if (updateBo.getStatus() != null) {
            if (updateBo.getStatus() != AccountConstants.STATUS_NORMAL
                    && updateBo.getStatus() != AccountConstants.STATUS_DISABLED) {
                throw new BusinessException("账号状态不合法");
            }
            account.setStatus(updateBo.getStatus());
        }
        account.setUpdateTime(new Date());
        accountMapper.updateById(account);
    }

    @Override
    public void delete(Long id, Long currentAdminId) {
        LoginAccount account = getAccountOrThrow(id);
        if (currentAdminId != null && currentAdminId.equals(account.getId())) {
            throw new BusinessException("不可删除当前登录的管理员账号");
        }
        accountMapper.deleteById(id);
    }

    @Override
    public AccountVo currentUserInfo(Long accountId) {
        return toVo(getAccountOrThrow(accountId));
    }

    private LoginAccount authenticate(AccountLoginBo loginBo) {
        if (loginBo == null) {
            throw new BusinessException("请输入账号/密码");
        }
        if (StrUtil.isBlank(loginBo.getUsername())) {
            throw new BusinessException("请输入账号");
        }
        if (StrUtil.isBlank(loginBo.getPassword())) {
            throw new BusinessException("请输入密码");
        }
        LoginAccount account = findByUsername(loginBo.getUsername().trim());
        if (account == null || !PasswordUtils.matches(loginBo.getPassword(), account.getPassword())) {
            throw new BusinessException("账号或密码错误");
        }
        if (account.getStatus() != null && account.getStatus() == AccountConstants.STATUS_DISABLED) {
            throw new BusinessException("账号已被禁用");
        }
        return account;
    }

    private AccountLoginVo buildLoginVo(LoginAccount account) {
        String token = tokenService.createToken(account.getId(), account.getUsername(), account.getRole());
        AccountLoginVo vo = new AccountLoginVo();
        vo.setToken(token);
        vo.setAccountId(account.getId());
        vo.setUsername(account.getUsername());
        vo.setRole(account.getRole());
        return vo;
    }

    private void validateSaveBo(AccountSaveBo saveBo) {
        if (saveBo == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (StrUtil.isBlank(saveBo.getUsername())) {
            throw new BusinessException("请输入账号");
        }
        if (StrUtil.isBlank(saveBo.getPassword())) {
            throw new BusinessException("请输入密码");
        }
    }

    private boolean existsByUsername(String username) {
        return findByUsername(username) != null;
    }

    private LoginAccount findByUsername(String username) {
        LambdaQueryWrapper<LoginAccount> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(LoginAccount::getUsername, username);
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
        vo.setStatusText(account.getStatus() != null && account.getStatus() == AccountConstants.STATUS_NORMAL
                ? "正常" : "禁用");
        return vo;
    }
}
