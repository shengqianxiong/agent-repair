package com.sqx.modules.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.auth.db.entity.AdminUser;
import com.sqx.modules.auth.db.mapper.AdminUserMapper;
import com.sqx.modules.auth.domain.bo.LoginBo;
import com.sqx.modules.auth.domain.vo.LoginVo;
import com.sqx.modules.auth.service.AdminUserService;
import com.sqx.modules.auth.util.PasswordUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Service;

/**
 * 管理端用户服务实现。
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

    private static final int STATUS_ENABLED = 1;

    private final AdminUserMapper adminUserMapper;

    public AdminUserServiceImpl(AdminUserMapper adminUserMapper) {
        this.adminUserMapper = adminUserMapper;
    }

    @Override
    public LoginVo login(LoginBo loginBo) {
        validateLoginBo(loginBo);
        AdminUser admin = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getUsername, loginBo.getUsername()));
        if (admin == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (admin.getStatus() == null || admin.getStatus() != STATUS_ENABLED) {
            throw new BusinessException(403, "账号已被禁用，请联系管理员");
        }
        if (!PasswordUtil.matches(loginBo.getPassword(), admin.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        Subject subject = SecurityUtils.getSubject();
        subject.login(new UsernamePasswordToken(loginBo.getUsername(), loginBo.getPassword()));
        return buildLoginVo(admin);
    }

    private void validateLoginBo(LoginBo loginBo) {
        if (loginBo == null || StrUtil.isBlank(loginBo.getUsername()) || StrUtil.isBlank(loginBo.getPassword())) {
            throw new BusinessException(400, "请输入用户名和密码");
        }
    }

    private LoginVo buildLoginVo(AdminUser admin) {
        LoginVo loginVo = new LoginVo();
        loginVo.setToken("admin:" + admin.getId());
        loginVo.setUserId(admin.getId());
        loginVo.setUsername(admin.getUsername());
        loginVo.setNickname(admin.getNickname());
        return loginVo;
    }
}
