package com.sqx.modules.login.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.PageResult;
import com.sqx.common.constant.AccountRole;
import com.sqx.common.constant.AccountStatus;
import com.sqx.common.constant.ClientType;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.login.db.entity.LoginAccount;
import com.sqx.modules.login.db.mapper.LoginAccountMapper;
import com.sqx.modules.login.domain.bo.AccountLoginBo;
import com.sqx.modules.login.domain.bo.AccountQueryBo;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.vo.AccountVo;
import com.sqx.modules.login.domain.vo.LoginVo;
import com.sqx.modules.login.service.LoginAccountService;
import com.sqx.modules.login.service.TokenService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 登录账号业务服务实现
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
    public LoginVo appLogin(AccountLoginBo loginBo) {
        LoginAccount account = authenticate(loginBo);
        String token = tokenService.createToken(account.getId(), ClientType.APP);
        return buildLoginVo(account, token);
    }

    @Override
    public LoginVo adminLogin(AccountLoginBo loginBo) {
        LoginAccount account = authenticate(loginBo);
        if (!Integer.valueOf(AccountRole.ADMIN).equals(account.getRole())) {
            throw new BusinessException(403, "无访问权限");
        }
        String token = tokenService.createToken(account.getId(), ClientType.ADMIN);
        return buildLoginVo(account, token);
    }

    @Override
    public PageResult<AccountVo> page(AccountQueryBo queryBo) {
        int page = queryBo.getPage() == null || queryBo.getPage() < 1 ? 1 : queryBo.getPage();
        int pageSize = queryBo.getPageSize() == null || queryBo.getPageSize() < 1 ? 10 : queryBo.getPageSize();

        LambdaQueryWrapper<LoginAccount> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(queryBo.getKeyword())) {
            wrapper.like(LoginAccount::getUsername, queryBo.getKeyword());
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
        validateUsername(saveBo.getUsername());
        if (StrUtil.isBlank(saveBo.getPassword())) {
            throw new BusinessException("请输入密码");
        }
        if (existsUsername(saveBo.getUsername())) {
            throw new BusinessException("账号已存在");
        }
        Date now = new Date();
        LoginAccount account = new LoginAccount();
        account.setUsername(saveBo.getUsername().trim());
        account.setPassword(encryptPassword(saveBo.getPassword()));
        account.setStatus(AccountStatus.NORMAL);
        account.setRole(AccountRole.USER);
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
            account.setPassword(encryptPassword(updateBo.getPassword()));
        }
        if (updateBo.getStatus() != null) {
            if (!Integer.valueOf(AccountStatus.DISABLED).equals(updateBo.getStatus())
                    && !Integer.valueOf(AccountStatus.NORMAL).equals(updateBo.getStatus())) {
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

    @Override
    public AccountVo currentAccount(Long accountId) {
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
        LoginAccount account = accountMapper.selectOne(new LambdaQueryWrapper<LoginAccount>()
                .eq(LoginAccount::getUsername, loginBo.getUsername().trim()));
        if (account == null) {
            throw new BusinessException("账号或密码错误");
        }
        if (Integer.valueOf(AccountStatus.DISABLED).equals(account.getStatus())) {
            throw new BusinessException("账号已被禁用");
        }
        if (!account.getPassword().equals(encryptPassword(loginBo.getPassword()))) {
            throw new BusinessException("账号或密码错误");
        }
        return account;
    }

    private void validateUsername(String username) {
        if (StrUtil.isBlank(username)) {
            throw new BusinessException("请输入账号");
        }
    }

    private boolean existsUsername(String username) {
        return accountMapper.selectCount(new LambdaQueryWrapper<LoginAccount>()
                .eq(LoginAccount::getUsername, username.trim())) > 0;
    }

    private LoginAccount getAccountOrThrow(Long id) {
        LoginAccount account = accountMapper.selectById(id);
        if (account == null) {
            throw new BusinessException("账号不存在");
        }
        return account;
    }

    private String encryptPassword(String rawPassword) {
        return DigestUtil.md5Hex(rawPassword);
    }

    private LoginVo buildLoginVo(LoginAccount account, String token) {
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        loginVo.setAccountId(account.getId());
        loginVo.setUsername(account.getUsername());
        return loginVo;
    }

    private AccountVo toVo(LoginAccount account) {
        AccountVo vo = new AccountVo();
        BeanUtil.copyProperties(account, vo);
        vo.setStatusText(resolveStatusText(account.getStatus()));
        return vo;
    }

    private String resolveStatusText(Integer status) {
        if (Integer.valueOf(AccountStatus.DISABLED).equals(status)) {
            return "禁用";
        }
        if (Integer.valueOf(AccountStatus.NORMAL).equals(status)) {
            return "正常";
        }
        return "未知";
    }
}
