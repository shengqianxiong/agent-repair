package com.sqx.modules.login.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
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
import com.sqx.modules.login.service.impl.AccountServiceImpl;
import com.sqx.modules.login.util.PasswordUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock
    private LoginAccountMapper loginAccountMapper;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private LoginAccount adminAccount;

    @BeforeEach
    void setUp() {
        adminAccount = new LoginAccount();
        adminAccount.setId(1L);
        adminAccount.setAccount("admin");
        adminAccount.setPassword(PasswordUtil.encrypt("admin123"));
        adminAccount.setStatus(AccountConstant.STATUS_NORMAL);
        adminAccount.setRole(AccountConstant.ROLE_ADMIN);
        adminAccount.setCreateTime(LocalDateTime.now());
    }

    @Test
    void login_shouldReturnTokenForValidAdmin() {
        LoginBo loginBo = new LoginBo();
        loginBo.setAccount("admin");
        loginBo.setPassword("admin123");
        when(loginAccountMapper.selectOne(any())).thenReturn(adminAccount);
        when(tokenService.createToken(1L)).thenReturn("token-abc");

        LoginVo loginVo = accountService.login(loginBo, true);

        assertEquals("token-abc", loginVo.getToken());
        assertEquals(1L, loginVo.getId());
    }

    @Test
    void login_shouldRejectNonAdminWhenRequireAdmin() {
        LoginAccount user = new LoginAccount();
        user.setId(2L);
        user.setAccount("user");
        user.setPassword(PasswordUtil.encrypt("user123"));
        user.setStatus(AccountConstant.STATUS_NORMAL);
        user.setRole(AccountConstant.ROLE_USER);

        LoginBo loginBo = new LoginBo();
        loginBo.setAccount("user");
        loginBo.setPassword("user123");
        when(loginAccountMapper.selectOne(any())).thenReturn(user);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> accountService.login(loginBo, true));
        assertEquals(403, ex.getCode());
    }

    @Test
    void page_shouldFilterByAccountKeyword() {
        LoginAccount user = new LoginAccount();
        user.setId(2L);
        user.setAccount("testuser");
        user.setStatus(AccountConstant.STATUS_NORMAL);
        user.setCreateTime(LocalDateTime.now());

        Page<LoginAccount> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(user));
        page.setTotal(1);
        when(loginAccountMapper.selectPage(any(Page.class), any())).thenReturn(page);

        AccountQueryBo queryBo = new AccountQueryBo();
        queryBo.setAccount("test");
        queryBo.setPage(1);
        queryBo.setPageSize(10);

        PageResult<AccountVo> result = accountService.page(queryBo);

        assertEquals(1, result.getTotal());
        assertEquals("testuser", result.getList().get(0).getAccount());
    }

    @Test
    void page_shouldUseDefaultPaginationWhenInvalid() {
        Page<LoginAccount> page = new Page<>(1, 10);
        page.setRecords(Arrays.asList(adminAccount));
        page.setTotal(1);
        when(loginAccountMapper.selectPage(any(Page.class), any())).thenReturn(page);

        AccountQueryBo queryBo = new AccountQueryBo();
        queryBo.setPage(0);
        queryBo.setPageSize(-1);

        PageResult<AccountVo> result = accountService.page(queryBo);

        assertEquals(1, result.getPage());
        assertEquals(10, result.getPageSize());
    }

    @Test
    void save_shouldRejectDuplicateAccount() {
        AccountSaveBo saveBo = new AccountSaveBo();
        saveBo.setAccount("admin");
        saveBo.setPassword("123456");
        when(loginAccountMapper.selectCount(any())).thenReturn(1L);

        assertThrows(BusinessException.class, () -> accountService.save(saveBo));
    }

    @Test
    void save_shouldInsertNewAccount() {
        AccountSaveBo saveBo = new AccountSaveBo();
        saveBo.setAccount("newuser");
        saveBo.setPassword("123456");
        when(loginAccountMapper.selectCount(any())).thenReturn(0L);

        accountService.save(saveBo);

        ArgumentCaptor<LoginAccount> captor = ArgumentCaptor.forClass(LoginAccount.class);
        verify(loginAccountMapper).insert(captor.capture());
        LoginAccount saved = captor.getValue();
        assertEquals("newuser", saved.getAccount());
        assertEquals(AccountConstant.ROLE_USER, saved.getRole());
        assertEquals(AccountConstant.STATUS_NORMAL, saved.getStatus());
    }

    @Test
    void delete_shouldRejectCurrentAdminAccount() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> accountService.delete(1L, 1L));
        assertEquals("不可删除当前登录的管理员账号", ex.getMessage());
        verify(loginAccountMapper, never()).deleteById(any(Long.class));
    }

    @Test
    void update_shouldChangeStatus() {
        AccountUpdateBo updateBo = new AccountUpdateBo();
        updateBo.setId(2L);
        updateBo.setStatus(AccountConstant.STATUS_DISABLED);
        LoginAccount account = new LoginAccount();
        account.setId(2L);
        account.setAccount("user");
        when(loginAccountMapper.selectById(2L)).thenReturn(account);

        accountService.update(updateBo);

        verify(loginAccountMapper).updateById(account);
        assertEquals(AccountConstant.STATUS_DISABLED, account.getStatus());
    }
}
