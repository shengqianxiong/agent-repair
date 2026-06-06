package com.sqx.modules.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.exception.BusinessException;
import com.sqx.common.utils.PasswordUtils;
import com.sqx.common.utils.TokenUtils;
import com.sqx.modules.auth.db.entity.AuthAdminUser;
import com.sqx.modules.auth.db.entity.AuthAppUser;
import com.sqx.modules.auth.db.mapper.AuthAdminUserMapper;
import com.sqx.modules.auth.db.mapper.AuthAppUserMapper;
import com.sqx.modules.auth.domain.bo.LoginBo;
import com.sqx.modules.auth.domain.vo.LoginVo;
import com.sqx.modules.auth.service.AuthService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 认证服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthAdminUserMapper authAdminUserMapper;
    private final AuthAppUserMapper authAppUserMapper;

    public AuthServiceImpl(AuthAdminUserMapper authAdminUserMapper,
                           AuthAppUserMapper authAppUserMapper) {
        this.authAdminUserMapper = authAdminUserMapper;
        this.authAppUserMapper = authAppUserMapper;
    }

    @Override
    public LoginVo adminLogin(LoginBo loginBo) {
        validateLoginBo(loginBo);
        AuthAdminUser user = authAdminUserMapper.selectOne(
                new LambdaQueryWrapper<AuthAdminUser>()
                        .eq(AuthAdminUser::getUsername, loginBo.getUsername()));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }
        if (!PasswordUtils.matches(loginBo.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        LoginVo vo = new LoginVo();
        vo.setToken(TokenUtils.generateAdminToken(user.getId()));
        vo.setUsername(user.getUsername());
        return vo;
    }

    @Override
    public LoginVo appLogin(LoginBo loginBo) {
        validateLoginBo(loginBo);
        AuthAppUser user = authAppUserMapper.selectOne(
                new LambdaQueryWrapper<AuthAppUser>()
                        .eq(AuthAppUser::getUsername, loginBo.getUsername()));
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用，请联系客服");
        }
        if (!PasswordUtils.matches(loginBo.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        LoginVo vo = new LoginVo();
        vo.setToken(TokenUtils.generateAppToken(user.getId()));
        vo.setUsername(user.getUsername());
        return vo;
    }

    private void validateLoginBo(LoginBo loginBo) {
        if (loginBo == null || !StringUtils.hasText(loginBo.getUsername())
                || !StringUtils.hasText(loginBo.getPassword())) {
            throw new BusinessException("用户名和密码不能为空");
        }
    }
}
