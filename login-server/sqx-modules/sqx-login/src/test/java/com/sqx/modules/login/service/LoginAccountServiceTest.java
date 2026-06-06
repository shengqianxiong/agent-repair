package com.sqx.modules.login.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.PageResult;
import com.sqx.common.constant.AccountRole;
import com.sqx.common.constant.AccountStatus;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.login.db.entity.LoginAccount;
import com.sqx.modules.login.db.mapper.LoginAccountMapper;
import com.sqx.modules.login.domain.bo.AccountLoginBo;
import com.sqx.modules.login.domain.bo.AccountQueryBo;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.vo.AccountVo;
import com.sqx.modules.login.domain.vo.LoginVo;
import com.sqx.modules.login.service.impl.LoginAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginAccountServiceTest {

    @Mock
    private LoginAccountMapper accountMapper;

    @Mock
    private TokenService tokenService;

    private LoginAccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new LoginAccountServiceImpl(accountMapper, tokenService);
    }

    @Test
    void appLogin_shouldReturnTokenWhenCredentialsValid() {
        LoginAccount account = buildAccount(1L, "user01", "6ad14ba9986e3615423dfca256d04e3f",
                AccountStatus.NORMAL, AccountRole.USER);
        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(account);
        when(tokenService.createToken(1L, 1)).thenReturn("token-app");

        AccountLoginBo loginBo = new AccountLoginBo();
        loginBo.setUsername("user01");
        loginBo.setPassword("user123");

        LoginVo loginVo = accountService.appLogin(loginBo);

        assertEquals("token-app", loginVo.getToken());
        assertEquals(1L, loginVo.getAccountId());
        assertEquals("user01", loginVo.getUsername());
    }

    @Test
    void appLogin_shouldThrowWhenPasswordWrong() {
        LoginAccount account = buildAccount(1L, "user01", "6ad14ba9986e3615423dfca256d04e3f",
                AccountStatus.NORMAL, AccountRole.USER);
        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(account);

        AccountLoginBo loginBo = new AccountLoginBo();
        loginBo.setUsername("user01");
        loginBo.setPassword("wrong");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> accountService.appLogin(loginBo));
        assertEquals("账号或密码错误", exception.getMessage());
    }

    @Test
    void appLogin_shouldThrowWhenAccountDisabled() {
        LoginAccount account = buildAccount(1L, "user01", "6ad14ba9986e3615423dfca256d04e3f",
                AccountStatus.DISABLED, AccountRole.USER);
        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(account);

        AccountLoginBo loginBo = new AccountLoginBo();
        loginBo.setUsername("user01");
        loginBo.setPassword("user123");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> accountService.appLogin(loginBo));
        assertEquals("账号已被禁用", exception.getMessage());
    }

    @Test
    void save_shouldThrowWhenUsernameExists() {
        when(accountMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        AccountSaveBo saveBo = new AccountSaveBo();
        saveBo.setUsername("user01");
        saveBo.setPassword("123456");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> accountService.save(saveBo));
        assertEquals("账号已存在", exception.getMessage());
    }

    @Test
    void save_shouldInsertAccountWhenUsernameUnique() {
        when(accountMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        AccountSaveBo saveBo = new AccountSaveBo();
        saveBo.setUsername("newuser");
        saveBo.setPassword("123456");

        accountService.save(saveBo);

        ArgumentCaptor<LoginAccount> captor = ArgumentCaptor.forClass(LoginAccount.class);
        verify(accountMapper).insert(captor.capture());
        LoginAccount saved = captor.getValue();
        assertEquals("newuser", saved.getUsername());
        assertEquals(AccountStatus.NORMAL, saved.getStatus());
        assertEquals(AccountRole.USER, saved.getRole());
        assertNotNull(saved.getPassword());
    }

    @Test
    void delete_shouldThrowWhenDeletingCurrentAdmin() {
        LoginAccount account = buildAccount(1L, "admin", "pwd", AccountStatus.NORMAL, AccountRole.ADMIN);
        when(accountMapper.selectById(1L)).thenReturn(account);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> accountService.delete(1L, 1L));
        assertEquals("不可删除当前登录的管理员账号", exception.getMessage());
    }

    @Test
    void page_shouldReturnPagedAccounts() {
        LoginAccount account = buildAccount(1L, "admin", "pwd", AccountStatus.NORMAL, AccountRole.ADMIN);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<LoginAccount> page =
                new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(1, 10);
        page.setRecords(Collections.singletonList(account));
        page.setTotal(1);
        when(accountMapper.selectPage(any(), any(LambdaQueryWrapper.class))).thenReturn(page);

        AccountQueryBo queryBo = new AccountQueryBo();
        queryBo.setPage(1);
        queryBo.setPageSize(10);

        PageResult<AccountVo> result = accountService.page(queryBo);

        assertEquals(1, result.getTotal());
        assertEquals(1, result.getList().size());
        assertEquals("admin", result.getList().get(0).getUsername());
    }

    @Test
    void update_shouldResetPasswordAndStatus() {
        LoginAccount account = buildAccount(2L, "user01", "old", AccountStatus.NORMAL, AccountRole.USER);
        when(accountMapper.selectById(2L)).thenReturn(account);

        AccountUpdateBo updateBo = new AccountUpdateBo();
        updateBo.setId(2L);
        updateBo.setPassword("newpass");
        updateBo.setStatus(AccountStatus.DISABLED);

        accountService.update(updateBo);

        verify(accountMapper).updateById(account);
        assertEquals(AccountStatus.DISABLED, account.getStatus());
    }

    private LoginAccount buildAccount(Long id, String username, String password, int status, int role) {
        LoginAccount account = new LoginAccount();
        account.setId(id);
        account.setUsername(username);
        account.setPassword(password);
        account.setStatus(status);
        account.setRole(role);
        account.setCreateTime(new Date());
        account.setUpdateTime(new Date());
        return account;
    }
}
