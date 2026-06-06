package com.sqx.modules.login.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.exception.BusinessException;
import com.sqx.framework.token.TokenService;
import com.sqx.modules.login.constant.AccountRole;
import com.sqx.modules.login.constant.AccountStatus;
import com.sqx.modules.login.db.entity.LoginAccount;
import com.sqx.modules.login.db.mapper.LoginAccountMapper;
import com.sqx.modules.login.domain.bo.LoginBo;
import com.sqx.modules.login.domain.vo.LoginVo;
import com.sqx.modules.login.service.impl.LoginAuthServiceImpl;
import com.sqx.modules.login.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
 * 登录认证服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class LoginAuthServiceTest {

    @Mock
    private LoginAccountMapper accountMapper;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private LoginAuthServiceImpl loginAuthService;

    private LoginBo validLoginBo;
    private LoginAccount normalAccount;
    private LoginAccount adminAccount;

    @BeforeEach
    void setUp() {
        validLoginBo = new LoginBo();
        validLoginBo.setUsername("user01");
        validLoginBo.setPassword("admin123");

        normalAccount = new LoginAccount();
        normalAccount.setId(2L);
        normalAccount.setUsername("user01");
        normalAccount.setPassword(PasswordUtil.encrypt("admin123"));
        normalAccount.setStatus(AccountStatus.NORMAL);
        normalAccount.setRole(AccountRole.USER);

        adminAccount = new LoginAccount();
        adminAccount.setId(1L);
        adminAccount.setUsername("admin");
        adminAccount.setPassword(PasswordUtil.encrypt("admin123"));
        adminAccount.setStatus(AccountStatus.NORMAL);
        adminAccount.setRole(AccountRole.ADMIN);
    }

    @Test
    void appLogin_shouldReturnToken_whenCredentialsValid() {
        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(normalAccount);
        when(tokenService.createToken(2L, AccountRole.USER)).thenReturn("mock-token");

        LoginVo loginVo = loginAuthService.appLogin(validLoginBo);

        assertNotNull(loginVo.getToken());
        assertEquals("mock-token", loginVo.getToken());
        assertEquals(2L, loginVo.getAccountId());
        verify(tokenService).createToken(2L, AccountRole.USER);
    }

    @Test
    void appLogin_shouldThrow_whenPasswordWrong() {
        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(normalAccount);
        validLoginBo.setPassword("wrong-password");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> loginAuthService.appLogin(validLoginBo));
        assertEquals("账号或密码错误", exception.getMessage());
    }

    @Test
    void appLogin_shouldThrow_whenAccountDisabled() {
        normalAccount.setStatus(AccountStatus.DISABLED);
        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(normalAccount);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> loginAuthService.appLogin(validLoginBo));
        assertEquals("账号已被禁用", exception.getMessage());
    }

    @Test
    void appLogin_shouldThrow_whenUsernameBlank() {
        validLoginBo.setUsername("");

        BusinessException exception = assertThrows(BusinessException.class,
                () -> loginAuthService.appLogin(validLoginBo));
        assertEquals("请输入账号", exception.getMessage());
    }

    @Test
    void adminLogin_shouldThrow_whenNotAdmin() {
        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(normalAccount);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> loginAuthService.adminLogin(validLoginBo));
        assertEquals(403, exception.getCode());
        assertEquals("无访问权限", exception.getMessage());
    }

    @Test
    void adminLogin_shouldReturnToken_whenAdminCredentialsValid() {
        validLoginBo.setUsername("admin");
        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(adminAccount);
        when(tokenService.createToken(1L, AccountRole.ADMIN)).thenReturn("admin-token");

        LoginVo loginVo = loginAuthService.adminLogin(validLoginBo);

        assertEquals("admin-token", loginVo.getToken());
        assertEquals("admin", loginVo.getUsername());
    }
}
