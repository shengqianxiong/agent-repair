package com.sqx.modules.login.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.constants.AccountConstants;
import com.sqx.common.exception.BusinessException;
import com.sqx.common.utils.PasswordUtils;
import com.sqx.framework.token.TokenService;
import com.sqx.modules.login.db.entity.LoginAccount;
import com.sqx.modules.login.db.mapper.LoginAccountMapper;
import com.sqx.modules.login.domain.bo.AccountLoginBo;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.vo.AccountLoginVo;
import com.sqx.modules.login.service.impl.LoginAccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 登录账号服务单元测试。
 */
@ExtendWith(MockitoExtension.class)
class LoginAccountServiceTest {

    @Mock
    private LoginAccountMapper accountMapper;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private LoginAccountServiceImpl accountService;

    private LoginAccount adminAccount;

    @BeforeEach
    void setUp() {
        adminAccount = new LoginAccount();
        adminAccount.setId(1L);
        adminAccount.setUsername("admin");
        adminAccount.setPassword(PasswordUtils.encrypt("admin123"));
        adminAccount.setStatus(AccountConstants.STATUS_NORMAL);
        adminAccount.setRole(AccountConstants.ROLE_ADMIN);
    }

    @Test
    void appLogin_success() {
        AccountLoginBo loginBo = new AccountLoginBo();
        loginBo.setUsername("admin");
        loginBo.setPassword("admin123");

        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(adminAccount);
        when(tokenService.createToken(1L, "admin", AccountConstants.ROLE_ADMIN)).thenReturn("token-001");

        AccountLoginVo result = accountService.appLogin(loginBo);

        assertNotNull(result);
        assertEquals("token-001", result.getToken());
        assertEquals(1L, result.getAccountId());
    }

    @Test
    void appLogin_wrongPassword() {
        AccountLoginBo loginBo = new AccountLoginBo();
        loginBo.setUsername("admin");
        loginBo.setPassword("wrong");

        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(adminAccount);

        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.appLogin(loginBo));
        assertEquals("账号或密码错误", ex.getMessage());
    }

    @Test
    void appLogin_disabledAccount() {
        adminAccount.setStatus(AccountConstants.STATUS_DISABLED);
        AccountLoginBo loginBo = new AccountLoginBo();
        loginBo.setUsername("admin");
        loginBo.setPassword("admin123");

        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(adminAccount);

        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.appLogin(loginBo));
        assertEquals("账号已被禁用", ex.getMessage());
    }

    @Test
    void adminLogin_notAdminRole() {
        adminAccount.setRole(AccountConstants.ROLE_USER);
        AccountLoginBo loginBo = new AccountLoginBo();
        loginBo.setUsername("admin");
        loginBo.setPassword("admin123");

        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(adminAccount);

        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.adminLogin(loginBo));
        assertEquals(403, ex.getCode());
        assertEquals("权限不足", ex.getMessage());
    }

    @Test
    void save_duplicateUsername() {
        AccountSaveBo saveBo = new AccountSaveBo();
        saveBo.setUsername("admin");
        saveBo.setPassword("123456");

        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(adminAccount);

        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.save(saveBo));
        assertEquals("账号已存在", ex.getMessage());
    }

    @Test
    void save_success() {
        AccountSaveBo saveBo = new AccountSaveBo();
        saveBo.setUsername("newuser");
        saveBo.setPassword("123456");

        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        accountService.save(saveBo);

        ArgumentCaptor<LoginAccount> captor = ArgumentCaptor.forClass(LoginAccount.class);
        verify(accountMapper).insert(captor.capture());
        LoginAccount saved = captor.getValue();
        assertEquals("newuser", saved.getUsername());
        assertEquals(AccountConstants.STATUS_NORMAL, saved.getStatus());
        assertEquals(AccountConstants.ROLE_USER, saved.getRole());
    }

    @Test
    void delete_currentAdminForbidden() {
        when(accountMapper.selectById(1L)).thenReturn(adminAccount);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> accountService.delete(1L, 1L));
        assertEquals("不可删除当前登录的管理员账号", ex.getMessage());
    }

    @Test
    void update_resetPassword() {
        when(accountMapper.selectById(2L)).thenReturn(adminAccount);

        AccountUpdateBo updateBo = new AccountUpdateBo();
        updateBo.setId(2L);
        updateBo.setPassword("newpass");

        accountService.update(updateBo);

        ArgumentCaptor<LoginAccount> captor = ArgumentCaptor.forClass(LoginAccount.class);
        verify(accountMapper).updateById(captor.capture());
        assertEquals(PasswordUtils.encrypt("newpass"), captor.getValue().getPassword());
    }
}
