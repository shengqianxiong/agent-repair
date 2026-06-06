package com.sqx.modules.login.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.exception.BusinessException;
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

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 登录账号服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class LoginAccountServiceTest {

    @Mock
    private LoginAccountMapper accountMapper;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private LoginAccountServiceImpl accountService;

    private LoginAccount normalAccount;

    @BeforeEach
    void setUp() {
        normalAccount = new LoginAccount();
        normalAccount.setId(1L);
        normalAccount.setUsername("admin");
        normalAccount.setPassword("0192023a7bbd73250516f069df18b500");
        normalAccount.setStatus(1);
        normalAccount.setCreateTime(new Date());
    }

    @Test
    void login_success() {
        AccountLoginBo loginBo = new AccountLoginBo();
        loginBo.setUsername("admin");
        loginBo.setPassword("admin123");

        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(normalAccount);
        when(tokenService.createToken(1L)).thenReturn("test-token");

        AccountLoginVo result = accountService.login(loginBo);

        assertNotNull(result);
        assertEquals("test-token", result.getToken());
        assertEquals(1L, result.getAccountId());
        assertEquals("admin", result.getUsername());
    }

    @Test
    void login_wrongPassword() {
        AccountLoginBo loginBo = new AccountLoginBo();
        loginBo.setUsername("admin");
        loginBo.setPassword("wrong");

        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(normalAccount);

        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.login(loginBo));
        assertEquals("账号或密码错误", ex.getMessage());
    }

    @Test
    void login_disabledAccount() {
        normalAccount.setStatus(0);
        AccountLoginBo loginBo = new AccountLoginBo();
        loginBo.setUsername("admin");
        loginBo.setPassword("admin123");

        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(normalAccount);

        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.login(loginBo));
        assertEquals("账号已被禁用", ex.getMessage());
    }

    @Test
    void login_emptyUsername() {
        AccountLoginBo loginBo = new AccountLoginBo();
        loginBo.setPassword("admin123");

        BusinessException ex = assertThrows(BusinessException.class, () -> accountService.login(loginBo));
        assertEquals("请输入账号", ex.getMessage());
    }

    @Test
    void save_duplicateUsername() {
        AccountSaveBo saveBo = new AccountSaveBo();
        saveBo.setUsername("admin");
        saveBo.setPassword("123456");

        when(accountMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(normalAccount);

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
        assertEquals(1, saved.getStatus());
    }

    @Test
    void update_resetPassword() {
        AccountUpdateBo updateBo = new AccountUpdateBo();
        updateBo.setId(1L);
        updateBo.setPassword("newpass");

        when(accountMapper.selectById(1L)).thenReturn(normalAccount);

        accountService.update(updateBo);

        ArgumentCaptor<LoginAccount> captor = ArgumentCaptor.forClass(LoginAccount.class);
        verify(accountMapper).updateById(captor.capture());
        assertEquals("e6053eb8d35e02ae40beeeacef203c1a", captor.getValue().getPassword());
    }

    @Test
    void delete_currentAccountForbidden() {
        when(accountMapper.selectById(1L)).thenReturn(normalAccount);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> accountService.delete(1L, 1L));
        assertEquals("不可删除当前登录的管理员账号", ex.getMessage());
    }

    @Test
    void page_returnsList() {
        Page<LoginAccount> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(normalAccount));
        page.setTotal(1);

        when(accountMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        assertEquals(1, accountService.page(new com.sqx.modules.login.domain.bo.AccountQueryBo()).getTotal());
    }
}
