package com.sqx.modules.login.service;

import com.sqx.common.PageResult;
import com.sqx.modules.login.domain.bo.AccountLoginBo;
import com.sqx.modules.login.domain.bo.AccountQueryBo;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.vo.AccountVo;
import com.sqx.modules.login.domain.vo.LoginVo;

/**
 * 登录账号业务服务
 */
public interface LoginAccountService {

    /**
     * 用户端登录
     */
    LoginVo appLogin(AccountLoginBo loginBo);

    /**
     * 管理端登录
     */
    LoginVo adminLogin(AccountLoginBo loginBo);

    /**
     * 分页查询账号列表
     */
    PageResult<AccountVo> page(AccountQueryBo queryBo);

    /**
     * 查询账号详情
     */
    AccountVo detail(Long id);

    /**
     * 新增账号
     */
    void save(AccountSaveBo saveBo);

    /**
     * 编辑账号（重置密码、修改状态）
     */
    void update(AccountUpdateBo updateBo);

    /**
     * 删除账号
     */
    void delete(Long id, Long currentAccountId);

    /**
     * 获取当前登录账号信息
     */
    AccountVo currentAccount(Long accountId);
}
