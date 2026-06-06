package com.sqx.modules.login.service;

import com.sqx.common.PageResult;
import com.sqx.modules.login.domain.bo.AccountLoginBo;
import com.sqx.modules.login.domain.bo.AccountQueryBo;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.vo.AccountLoginVo;
import com.sqx.modules.login.domain.vo.AccountVo;

/**
 * 登录账号业务服务。
 */
public interface LoginAccountService {

    /**
     * 用户端登录，校验账号密码与状态。
     *
     * @param loginBo 登录参数
     * @return 登录结果（含 Token）
     */
    AccountLoginVo appLogin(AccountLoginBo loginBo);

    /**
     * 管理端登录，仅管理员角色可登录。
     *
     * @param loginBo 登录参数
     * @return 登录结果（含 Token）
     */
    AccountLoginVo adminLogin(AccountLoginBo loginBo);

    /**
     * 分页查询账号列表。
     *
     * @param queryBo 查询参数
     * @return 分页结果
     */
    PageResult<AccountVo> page(AccountQueryBo queryBo);

    /**
     * 查询账号详情。
     *
     * @param id 账号主键
     * @return 账号信息
     */
    AccountVo detail(Long id);

    /**
     * 新增账号。
     *
     * @param saveBo 新增参数
     */
    void save(AccountSaveBo saveBo);

    /**
     * 编辑账号（重置密码、修改状态）。
     *
     * @param updateBo 编辑参数
     */
    void update(AccountUpdateBo updateBo);

    /**
     * 删除账号，不可删除当前登录管理员。
     *
     * @param id 账号主键
     * @param currentAdminId 当前登录管理员 ID
     */
    void delete(Long id, Long currentAdminId);

    /**
     * 获取当前登录用户账号信息（App 端）。
     *
     * @param accountId 当前用户 ID
     * @return 账号信息
     */
    AccountVo currentUserInfo(Long accountId);
}
