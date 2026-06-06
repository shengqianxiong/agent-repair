package com.sqx.modules.login.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.exception.BusinessException;
import com.sqx.common.utils.AdminContext;
import com.sqx.modules.login.constant.AccountStatus;
import com.sqx.modules.login.db.entity.LoginAccount;
import com.sqx.modules.login.db.mapper.LoginAccountMapper;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.vo.AccountVo;
import com.sqx.modules.login.service.impl.LoginAccountServiceImpl;
import com.sqx.modules.login.util.PasswordUtil;
import org.junit.jupiter.api.AfterEach;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 账号管理服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class LoginAccountServiceTest {

    @Mock
    private LoginAccountMapper accountMapper;

    @InjectMocks
    private LoginAccountServiceImpl accountService;

    private LoginAccount existingAccount;

    @BeforeEach
    void setUp() {
        existingAccount = new LoginAccount();
        existingAccount.setId(1L);
        existingAccount.setUsername("admin");
        existingAccount.setPassword(PasswordUtil.encrypt("admin123"));
        existingAccount.setStatus(AccountStatus.NORMAL);
        existingAccount.setRole(1);
        existingAccount.setCreateTime(new Date());
    }

    @AfterEach
    void tearDown() {
        AdminContext.clear();
    }

    @Test
    void save_shouldInsertAccount_whenUsernameUnique() {
        AccountSaveBo saveBo = new AccountSaveBo();
        saveBo.setUsername("newuser");
        saveBo.setPassword("123456");
        when(accountMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(0L);

        accountService.save(saveBo);

        ArgumentCaptor<LoginAccount> captor = ArgumentCaptor.forClass(LoginAccount.class);
        verify(accountMapper).insert(captor.capture());
        LoginAccount inserted = captor.getValue();
        assertEquals("newuser", inserted.getUsername());
        assertEquals(AccountStatus.NORMAL, inserted.getStatus());
        assertEquals(PasswordUtil.encrypt("123456"), inserted.getPassword());
    }

    @Test
    void save_shouldThrow_whenUsernameExists() {
        AccountSaveBo saveBo = new AccountSaveBo();
        saveBo.setUsername("admin");
        saveBo.setPassword("123456");
        when(accountMapper.selectCount(any(LambdaQueryWrapper.class))).thenReturn(1L);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> accountService.save(saveBo));
        assertEquals("账号已存在", exception.getMessage());
        verify(accountMapper, never()).insert(any(LoginAccount.class));
    }

    @Test
    void update_shouldChangePassword_whenPasswordProvided() {
        AccountUpdateBo updateBo = new AccountUpdateBo();
        updateBo.setId(1L);
        updateBo.setPassword("newpass");
        when(accountMapper.selectById(1L)).thenReturn(existingAccount);

        accountService.update(updateBo);

        verify(accountMapper).updateById(existingAccount);
        assertEquals(PasswordUtil.encrypt("newpass"), existingAccount.getPassword());
    }

    @Test
    void delete_shouldThrow_whenDeletingCurrentAdmin() {
        AdminContext.setAdminId(1L);
        when(accountMapper.selectById(1L)).thenReturn(existingAccount);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> accountService.delete(1L));
        assertEquals("不可删除当前登录的管理员账号", exception.getMessage());
        verify(accountMapper, never()).deleteById(1L);
    }

    @Test
    void detail_shouldReturnAccountVo_whenAccountExists() {
        when(accountMapper.selectById(1L)).thenReturn(existingAccount);

        AccountVo accountVo = accountService.detail(1L);

        assertEquals("admin", accountVo.getUsername());
        assertEquals("正常", accountVo.getStatusText());
    }

    @Test
    void page_shouldReturnPagedResult() {
        Page<LoginAccount> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(existingAccount));
        page.setTotal(1);
        when(accountMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class))).thenReturn(page);

        assertEquals(1, accountService.page(null).getTotal());
        assertEquals("admin", accountService.page(null).getList().get(0).getUsername());
    }
}
