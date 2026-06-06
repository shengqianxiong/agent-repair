package com.sqx.modules.bar.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.bar.db.entity.BarUser;
import com.sqx.modules.bar.db.mapper.BarUserMapper;
import com.sqx.modules.bar.domain.bo.UserLoginBo;
import com.sqx.modules.bar.domain.vo.UserVo;
import com.sqx.modules.bar.service.BarUserService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BarUserServiceImpl implements BarUserService {

    private final BarUserMapper userMapper;

    public BarUserServiceImpl(BarUserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserVo login(UserLoginBo bo) {
        if (StrUtil.isBlank(bo.getOpenid())) {
            throw new BusinessException("openid不能为空");
        }
        BarUser user = userMapper.selectOne(new LambdaQueryWrapper<BarUser>()
                .eq(BarUser::getOpenid, bo.getOpenid()));
        Date now = new Date();
        if (user == null) {
            user = new BarUser();
            user.setOpenid(bo.getOpenid());
            user.setNickname(StrUtil.blankToDefault(bo.getNickname(), "酒友"));
            user.setAvatar(bo.getAvatar());
            user.setMemberLevel(1);
            user.setMemberLevelName("普通会员");
            user.setPoints(0);
            user.setStatus("online");
            user.setCreateTime(now);
            user.setUpdateTime(now);
            userMapper.insert(user);
        } else {
            if (StrUtil.isNotBlank(bo.getNickname())) {
                user.setNickname(bo.getNickname());
            }
            if (StrUtil.isNotBlank(bo.getAvatar())) {
                user.setAvatar(bo.getAvatar());
            }
            user.setStatus("online");
            user.setUpdateTime(now);
            userMapper.updateById(user);
        }
        return toVo(user, true);
    }

    @Override
    public UserVo info(Long userId) {
        BarUser user = getUserOrThrow(userId);
        return toVo(user, false);
    }

    private BarUser getUserOrThrow(Long userId) {
        BarUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return user;
    }

    private UserVo toVo(BarUser user, boolean withToken) {
        UserVo vo = new UserVo();
        BeanUtil.copyProperties(user, vo);
        if (withToken) {
            vo.setToken("uid:" + user.getId());
        }
        return vo;
    }
}
