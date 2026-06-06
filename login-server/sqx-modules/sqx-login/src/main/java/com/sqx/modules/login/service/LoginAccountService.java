package com.sqx.modules.login.service;

import com.sqx.common.PageResult;
import com.sqx.modules.login.domain.bo.AccountLoginBo;
import com.sqx.modules.login.domain.bo.AccountQueryBo;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.vo.AccountLoginVo;
import com.sqx.modules.login.domain.vo.AccountVo;

/**
 * 登录账号业务服务
 */
public interface LoginAccountService {

    /**
     * 账号登录
     *
     * @param loginBo 登录参数
     * @return 登录结果（含 Token）
     */
    AccountLoginVo login(AccountLoginBo loginBo);

    /**
     * 分页查询账号列表
     *
     * @param queryBo 查询条件
     * @return 分页结果
     */
    PageResult<AccountVo> page(AccountQueryBo queryBo);

    /**
     * 查询账号详情
     *
     * @param id 账号主键
     * @return 账号信息
     */
    AccountVo detail(Long id);

    /**
     * 新增账号
     *
     * @param saveBo 新增参数
     */
    void save(AccountSaveBo saveBo);

    /**
     * 编辑账号（重置密码、修改状态）
     *
     * @param updateBo 编辑参数
     */
    void update(AccountUpdateBo updateBo);

    /**
     * 删除账号
     *
     * @param id              账号主键
     * @param currentAccountId 当前登录管理员账号 ID
     */
    void delete(Long id, Long currentAccountId);
}
