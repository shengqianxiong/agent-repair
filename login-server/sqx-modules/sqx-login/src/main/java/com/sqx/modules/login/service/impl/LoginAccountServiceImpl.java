package com.sqx.modules.login.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.common.utils.AdminContext;
import com.sqx.modules.login.constant.AccountRole;
import com.sqx.modules.login.constant.AccountStatus;
import com.sqx.modules.login.db.entity.LoginAccount;
import com.sqx.modules.login.db.mapper.LoginAccountMapper;
import com.sqx.modules.login.domain.bo.AccountQueryBo;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.vo.AccountVo;
import com.sqx.modules.login.service.LoginAccountService;
import com.sqx.modules.login.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 账号管理服务实现
 */
@Service
public class LoginAccountServiceImpl implements LoginAccountService {

    private final LoginAccountMapper accountMapper;

    public LoginAccountServiceImpl(LoginAccountMapper accountMapper) {
        this.accountMapper = accountMapper;
    }

    @Override
    public PageResult<AccountVo> page(AccountQueryBo queryBo) {
        int page = queryBo == null || queryBo.getPage() == null || queryBo.getPage() < 1
                ? 1 : queryBo.getPage();
        int pageSize = queryBo == null || queryBo.getPageSize() == null || queryBo.getPageSize() < 1
                ? 10 : queryBo.getPageSize();

        LambdaQueryWrapper<LoginAccount> wrapper = new LambdaQueryWrapper<>();
        if (queryBo != null && StrUtil.isNotBlank(queryBo.getKeyword())) {
            wrapper.like(LoginAccount::getUsername, queryBo.getKeyword().trim());
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
        Long count = accountMapper.selectCount(new LambdaQueryWrapper<LoginAccount>()
                .eq(LoginAccount::getUsername, saveBo.getUsername().trim()));
        if (count != null && count > 0) {
            throw new BusinessException("账号已存在");
        }

        Date now = new Date();
        LoginAccount account = new LoginAccount();
        account.setUsername(saveBo.getUsername().trim());
        account.setPassword(PasswordUtil.encrypt(saveBo.getPassword()));
        account.setStatus(AccountStatus.NORMAL);
        account.setRole(AccountRole.USER);
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
        boolean changed = false;
        if (StrUtil.isNotBlank(updateBo.getPassword())) {
            account.setPassword(PasswordUtil.encrypt(updateBo.getPassword()));
            changed = true;
        }
        if (updateBo.getStatus() != null) {
            if (updateBo.getStatus() != AccountStatus.DISABLED
                    && updateBo.getStatus() != AccountStatus.NORMAL) {
                throw new BusinessException("账号状态不合法");
            }
            account.setStatus(updateBo.getStatus());
            changed = true;
        }
        if (!changed) {
            throw new BusinessException("请至少修改密码或账号状态");
        }
        account.setUpdateTime(new Date());
        accountMapper.updateById(account);
    }

    @Override
    public void delete(Long id) {
        LoginAccount account = getAccountOrThrow(id);
        Long currentAdminId = AdminContext.getAdminId();
        if (currentAdminId != null && currentAdminId.equals(account.getId())) {
            throw new BusinessException("不可删除当前登录的管理员账号");
        }
        accountMapper.deleteById(id);
    }

    @Override
    public AccountVo currentDetail(Long accountId) {
        if (accountId == null) {
            throw new BusinessException(401, "请先登录");
        }
        return detail(accountId);
    }

    private void validateSaveBo(AccountSaveBo saveBo) {
        if (saveBo == null) {
            throw new BusinessException("请求参数不能为空");
        }
        if (StrUtil.isBlank(saveBo.getUsername())) {
            throw new BusinessException("请输入账号");
        }
        if (StrUtil.isBlank(saveBo.getPassword())) {
            throw new BusinessException("请输入初始密码");
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
        vo.setStatusText(AccountStatus.isNormal(account.getStatus()) ? "正常" : "禁用");
        return vo;
    }
}
