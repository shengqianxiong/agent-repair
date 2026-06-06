package com.sqx.modules.auth.service.impl;

import cn.hutool.core.util.StrUtil;
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

/**
 * 登录认证服务实现
 */
@Service
public class AuthServiceImpl implements AuthService {

    private static final Integer USER_STATUS_ENABLED = 1;

    private final AuthAdminUserMapper adminUserMapper;
    private final AuthAppUserMapper appUserMapper;

    public AuthServiceImpl(AuthAdminUserMapper adminUserMapper, AuthAppUserMapper appUserMapper) {
        this.adminUserMapper = adminUserMapper;
        this.appUserMapper = appUserMapper;
    }

    @Override
    public LoginVo adminLogin(LoginBo loginBo) {
        validateLoginBo(loginBo);
        AuthAdminUser adminUser = adminUserMapper.selectOne(new LambdaQueryWrapper<AuthAdminUser>()
                .eq(AuthAdminUser::getUsername, loginBo.getUsername().trim()));
        if (adminUser == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (!USER_STATUS_ENABLED.equals(adminUser.getStatus())) {
            throw new BusinessException(403, "账号已被禁用，请联系管理员");
        }
        if (!PasswordUtils.matches(loginBo.getPassword(), adminUser.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        return buildLoginVo(adminUser.getId(), adminUser.getUsername(), adminUser.getNickname(),
                TokenUtils.generateAdminToken(adminUser.getId()));
    }

    @Override
    public LoginVo appLogin(LoginBo loginBo) {
        validateLoginBo(loginBo);
        AuthAppUser appUser = appUserMapper.selectOne(new LambdaQueryWrapper<AuthAppUser>()
                .eq(AuthAppUser::getUsername, loginBo.getUsername().trim()));
        if (appUser == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (!USER_STATUS_ENABLED.equals(appUser.getStatus())) {
            throw new BusinessException(403, "账号已被禁用，请联系客服");
        }
        if (!PasswordUtils.matches(loginBo.getPassword(), appUser.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        return buildLoginVo(appUser.getId(), appUser.getUsername(), appUser.getNickname(),
                TokenUtils.generateAppToken(appUser.getId()));
    }

    /**
     * 校验登录参数
     */
    private void validateLoginBo(LoginBo loginBo) {
        if (loginBo == null) {
            throw new BusinessException("请输入用户名和密码");
        }
        if (StrUtil.isBlank(loginBo.getUsername())) {
            throw new BusinessException("请输入用户名");
        }
        if (StrUtil.isBlank(loginBo.getPassword())) {
            throw new BusinessException("请输入密码");
        }
    }

    /**
     * 构建登录响应
     */
    private LoginVo buildLoginVo(Long userId, String username, String nickname, String token) {
        LoginVo loginVo = new LoginVo();
        loginVo.setUserId(userId);
        loginVo.setUsername(username);
        loginVo.setNickname(nickname);
        loginVo.setToken(token);
        return loginVo;
    }
}
