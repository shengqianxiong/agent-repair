package com.sqx.modules.login.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.constant.ClientType;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.login.db.entity.LoginToken;
import com.sqx.modules.login.db.mapper.LoginTokenMapper;
import com.sqx.modules.login.service.impl.TokenServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @Mock
    private LoginTokenMapper tokenMapper;

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenServiceImpl(tokenMapper);
    }

    @Test
    void createToken_shouldPersistTokenWithTwoHourExpiry() {
        String token = tokenService.createToken(1L, ClientType.APP);

        assertNotNull(token);
        ArgumentCaptor<LoginToken> captor = ArgumentCaptor.forClass(LoginToken.class);
        verify(tokenMapper).insert(captor.capture());
        LoginToken saved = captor.getValue();
        assertEquals(1L, saved.getAccountId());
        assertEquals(ClientType.APP, saved.getClientType());
        assertNotNull(saved.getExpireTime());
    }

    @Test
    void validateToken_shouldReturnAccountIdWhenTokenValid() {
        LoginToken loginToken = new LoginToken();
        loginToken.setAccountId(2L);
        loginToken.setClientType(ClientType.ADMIN);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        loginToken.setExpireTime(calendar.getTime());
        when(tokenMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(loginToken);

        Long accountId = tokenService.validateToken("valid-token", ClientType.ADMIN);

        assertEquals(2L, accountId);
    }

    @Test
    void validateToken_shouldThrowWhenTokenExpired() {
        LoginToken loginToken = new LoginToken();
        loginToken.setAccountId(2L);
        loginToken.setClientType(ClientType.ADMIN);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        loginToken.setExpireTime(calendar.getTime());
        when(tokenMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(loginToken);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> tokenService.validateToken("expired-token", ClientType.ADMIN));
        assertEquals("登录已过期，请重新登录", exception.getMessage());
    }

    @Test
    void validateToken_shouldThrowWhenTokenNotFound() {
        when(tokenMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        BusinessException exception = assertThrows(BusinessException.class,
                () -> tokenService.validateToken("missing", ClientType.APP));
        assertEquals("登录态无效", exception.getMessage());
    }
}
