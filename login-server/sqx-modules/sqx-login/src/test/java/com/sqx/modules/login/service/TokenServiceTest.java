package com.sqx.modules.login.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.login.db.entity.LoginToken;
import com.sqx.modules.login.db.mapper.LoginTokenMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private LoginTokenMapper loginTokenMapper;

    @InjectMocks
    private TokenService tokenService;

    @Test
    void createToken_success() {
        String token = tokenService.createToken(1L);
        assertNotNull(token);
        verify(loginTokenMapper).insert(any(LoginToken.class));
    }

    @Test
    void validateToken_success() {
        LoginToken loginToken = new LoginToken();
        loginToken.setId(1L);
        loginToken.setAccountId(2L);
        loginToken.setToken("valid-token");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 1);
        loginToken.setExpireTime(calendar.getTime());

        when(loginTokenMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(loginToken);

        assertEquals(2L, tokenService.validateToken("valid-token"));
    }

    @Test
    void validateToken_expired() {
        LoginToken loginToken = new LoginToken();
        loginToken.setId(1L);
        loginToken.setAccountId(2L);
        loginToken.setToken("expired-token");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, -1);
        loginToken.setExpireTime(calendar.getTime());

        when(loginTokenMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(loginToken);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> tokenService.validateToken("expired-token"));
        assertEquals("登录已过期，请重新登录", ex.getMessage());
    }

    @Test
    void validateToken_notFound() {
        when(loginTokenMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> tokenService.validateToken("missing"));
        assertEquals("登录态无效，请重新登录", ex.getMessage());
    }
}
