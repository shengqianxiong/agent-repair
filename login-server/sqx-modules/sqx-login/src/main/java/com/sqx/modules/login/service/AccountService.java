package com.sqx.modules.login.service;

import com.sqx.common.PageResult;
import com.sqx.modules.login.db.entity.LoginAccount;
import com.sqx.modules.login.domain.bo.AccountQueryBo;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.bo.LoginBo;
import com.sqx.modules.login.domain.vo.AccountVo;
import com.sqx.modules.login.domain.vo.LoginVo;

/**
 * 账号业务服务
 */
public interface AccountService {

    /**
     * 用户端登录
     *
     * @param loginBo 登录参数
     * @return 登录结果
     */
    LoginVo appLogin(LoginBo loginBo);

    /**
     * 管理端登录
     *
     * @param loginBo 登录参数
     * @return 登录结果
     */
    LoginVo adminLogin(LoginBo loginBo);

    /**
     * 分页查询账号列表
     *
     * @param queryBo 查询参数
     * @return 分页结果
     */
    PageResult<AccountVo> page(AccountQueryBo queryBo);

    /**
     * 查询账号详情
     *
     * @param id 账号ID
     * @return 账号详情
     */
    AccountVo detail(Long id);

    /**
     * 新增账号
     *
     * @param saveBo 新增参数
     */
    void save(AccountSaveBo saveBo);

    /**
     * 编辑账号
     *
     * @param updateBo 编辑参数
     */
    void update(AccountUpdateBo updateBo);

    /**
     * 删除账号
     *
     * @param id              账号ID
     * @param currentAccountId 当前登录管理员ID
     */
    void delete(Long id, Long currentAccountId);

    /**
     * 根据ID查询账号实体
     *
     * @param id 账号ID
     * @return 账号实体
     */
    LoginAccount getById(Long id);
}
