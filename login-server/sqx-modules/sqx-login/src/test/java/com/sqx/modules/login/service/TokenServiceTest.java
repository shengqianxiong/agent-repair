package com.sqx.modules.login.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.login.db.entity.LoginToken;
import com.sqx.modules.login.db.mapper.LoginTokenMapper;
import com.sqx.modules.login.service.impl.TokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Token 服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private LoginTokenMapper tokenMapper;

    @InjectMocks
    private TokenServiceImpl tokenService;

    @Test
    void createToken_shouldPersistTokenWithExpireTime() {
        when(tokenMapper.insert(any(LoginToken.class))).thenReturn(1);

        String token = tokenService.createToken(100L);

        assertNotNull(token);
        ArgumentCaptor<LoginToken> captor = ArgumentCaptor.forClass(LoginToken.class);
        verify(tokenMapper).insert(captor.capture());
        LoginToken saved = captor.getValue();
        assertEquals(100L, saved.getAccountId());
        assertEquals(token, saved.getToken());
        assertNotNull(saved.getExpireTime());
    }

    @Test
    void validateAndGetAccountId_shouldReturnAccountId_whenTokenValid() {
        LoginToken loginToken = new LoginToken();
        loginToken.setId(1L);
        loginToken.setToken("valid-token");
        loginToken.setAccountId(100L);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        loginToken.setExpireTime(calendar.getTime());

        when(tokenMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(loginToken);

        assertEquals(100L, tokenService.validateAndGetAccountId("valid-token"));
    }

    @Test
    void validateAndGetAccountId_shouldThrow_whenTokenExpired() {
        LoginToken loginToken = new LoginToken();
        loginToken.setId(1L);
        loginToken.setToken("expired-token");
        loginToken.setAccountId(100L);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        loginToken.setExpireTime(calendar.getTime());

        when(tokenMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(loginToken);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> tokenService.validateAndGetAccountId("expired-token"));
        assertEquals(401, ex.getCode());
        assertEquals("登录已过期，请重新登录", ex.getMessage());
        verify(tokenMapper).deleteById(1L);
    }

    @Test
    void validateAndGetAccountId_shouldThrow_whenTokenNotFound() {
        when(tokenMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> tokenService.validateAndGetAccountId("missing-token"));
        assertEquals(401, ex.getCode());
        assertEquals("登录态无效", ex.getMessage());
    }
}
