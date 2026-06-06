package com.sqx.modules.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.auth.db.entity.AppUser;
import com.sqx.modules.auth.db.mapper.AppUserMapper;
import com.sqx.modules.auth.domain.bo.LoginBo;
import com.sqx.modules.auth.domain.vo.LoginVo;
import com.sqx.modules.auth.service.AppUserService;
import com.sqx.modules.auth.util.PasswordUtil;
import org.springframework.stereotype.Service;

/**
 * App 端用户服务实现。
 */
@Service
public class AppUserServiceImpl implements AppUserService {

    private static final int STATUS_ENABLED = 1;

    private final AppUserMapper appUserMapper;

    public AppUserServiceImpl(AppUserMapper appUserMapper) {
        this.appUserMapper = appUserMapper;
    }

    @Override
    public LoginVo login(LoginBo loginBo) {
        validateLoginBo(loginBo);
        AppUser user = appUserMapper.selectOne(new LambdaQueryWrapper<AppUser>()
                .eq(AppUser::getUsername, loginBo.getUsername()));
        if (user == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != STATUS_ENABLED) {
            throw new BusinessException(403, "账号已被禁用，请联系客服");
        }
        if (!PasswordUtil.matches(loginBo.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        return buildLoginVo(user);
    }

    private void validateLoginBo(LoginBo loginBo) {
        if (loginBo == null || StrUtil.isBlank(loginBo.getUsername()) || StrUtil.isBlank(loginBo.getPassword())) {
            throw new BusinessException(400, "请输入用户名和密码");
        }
    }

    private LoginVo buildLoginVo(AppUser user) {
        LoginVo loginVo = new LoginVo();
        loginVo.setToken("uid:" + user.getId());
        loginVo.setUserId(user.getId());
        loginVo.setUsername(user.getUsername());
        loginVo.setNickname(user.getNickname());
        return loginVo;
    }
}
