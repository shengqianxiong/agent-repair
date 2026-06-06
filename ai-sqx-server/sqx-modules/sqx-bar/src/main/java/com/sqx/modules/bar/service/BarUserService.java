package com.sqx.modules.bar.service;

import com.sqx.modules.bar.domain.bo.UserLoginBo;
import com.sqx.modules.bar.domain.vo.UserVo;

public interface BarUserService {

    UserVo login(UserLoginBo bo);

    UserVo info(Long userId);
}
