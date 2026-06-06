package com.sqx.modules.login.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.constant.AccountConstant;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.login.db.entity.LoginAccount;
import com.sqx.modules.login.db.mapper.LoginAccountMapper;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.bo.LoginBo;
import com.sqx.modules.login.domain.vo.LoginVo;
import com.sqx.modules.login.service.impl.AccountServiceImpl;
import com.sqx.modules.login.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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

/**
 * 账号服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private LoginAccountMapper accountMapper;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private LoginAccount normalAccount;
    private LoginAccount adminAccount;

    @BeforeEach
    void setUp() {
        normalAccount = buildAccount(1L, "user", "user123", AccountConstant.ROLE_USER,
                AccountConstant.STATUS_NORMAL);
        adminAccount = buildAccount(2L, "admin", "admin123", AccountConstant.ROLE_ADMIN,
                AccountConstant.STATUS_NORMAL);
    }

    @Test
    void appLogin_shouldReturnToken_whenCredentialsValid() {
        LoginBo loginBo = new LoginBo();
        loginBo.setUsername("user");
        loginBo.setPassword("user123");

        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(normalAccount);
        when(tokenService.createToken(1L)).thenReturn("token-001");

        LoginVo loginVo = accountService.appLogin(loginBo);

        assertEquals("token-001", loginVo.getToken());
        assertEquals(1L, loginVo.getAccountId());
        assertEquals("user", loginVo.getUsername());
    }

    @Test
    void appLogin_shouldThrow_whenPasswordInvalid() {
        LoginBo loginBo = new LoginBo();
        loginBo.setUsername("user");
        loginBo.setPassword("wrong");

        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(normalAccount);

        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.appLogin(loginBo));
        assertEquals("账号或密码错误", ex.getMessage());
    }

    @Test
    void appLogin_shouldThrow_whenAccountDisabled() {
        normalAccount.setStatus(AccountConstant.STATUS_DISABLED);
        LoginBo loginBo = new LoginBo();
        loginBo.setUsername("user");
        loginBo.setPassword("user123");

        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(normalAccount);

        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.appLogin(loginBo));
        assertEquals("账号已被禁用", ex.getMessage());
    }

    @Test
    void adminLogin_shouldThrow_whenNotAdmin() {
        LoginBo loginBo = new LoginBo();
        loginBo.setUsername("user");
        loginBo.setPassword("user123");

        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(normalAccount);

        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.adminLogin(loginBo));
        assertEquals(403, ex.getCode());
        assertEquals("无访问权限", ex.getMessage());
    }

    @Test
    void save_shouldEncryptPasswordAndSetDefaultStatus() {
        AccountSaveBo saveBo = new AccountSaveBo();
        saveBo.setUsername("newuser");
        saveBo.setPassword("123456");

        when(accountMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        accountService.save(saveBo);

        ArgumentCaptor<LoginAccount> captor = ArgumentCaptor.forClass(LoginAccount.class);
        verify(accountMapper).insert(captor.capture());
        LoginAccount saved = captor.getValue();
        assertEquals("newuser", saved.getUsername());
        assertEquals(PasswordUtil.encrypt("123456"), saved.getPassword());
        assertEquals(AccountConstant.STATUS_NORMAL, saved.getStatus());
        assertNotNull(saved.getCreateTime());
    }

    @Test
    void save_shouldThrow_whenUsernameExists() {
        AccountSaveBo saveBo = new AccountSaveBo();
        saveBo.setUsername("user");
        saveBo.setPassword("123456");

        when(accountMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.save(saveBo));
        assertEquals("账号已存在", ex.getMessage());
    }

    @Test
    void delete_shouldThrow_whenDeletingCurrentAdmin() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> accountService.delete(2L, 2L));
        assertEquals("不可删除当前登录的管理员账号", ex.getMessage());
    }

    @Test
    void update_shouldResetPassword_whenPasswordProvided() {
        AccountUpdateBo updateBo = new AccountUpdateBo();
        updateBo.setId(1L);
        updateBo.setPassword("newpass");

        when(accountMapper.selectById(1L)).thenReturn(normalAccount);

        accountService.update(updateBo);

        ArgumentCaptor<LoginAccount> captor = ArgumentCaptor.forClass(LoginAccount.class);
        verify(accountMapper).updateById(captor.capture());
        assertEquals(PasswordUtil.encrypt("newpass"), captor.getValue().getPassword());
    }

    @Test
    void page_shouldReturnPagedResult() {
        Page<LoginAccount> pageData = new Page<>(1, 10);
        pageData.setRecords(Collections.singletonList(normalAccount));
        pageData.setTotal(1);

        when(accountMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(pageData);

        assertEquals(1, accountService.page(new com.sqx.modules.login.domain.bo.AccountQueryBo()).getList().size());
    }

    private LoginAccount buildAccount(Long id, String username, String rawPassword, int role, int status) {
        LoginAccount account = new LoginAccount();
        account.setId(id);
        account.setUsername(username);
        account.setPassword(PasswordUtil.encrypt(rawPassword));
        account.setRole(role);
        account.setStatus(status);
        account.setCreateTime(new Date());
        account.setUpdateTime(new Date());
        return account;
    }
}
