package com.sqx.modules.login.service;

import com.sqx.common.PageResult;
import com.sqx.modules.login.domain.bo.AccountQueryBo;
import com.sqx.modules.login.domain.bo.AccountSaveBo;
import com.sqx.modules.login.domain.bo.AccountUpdateBo;
import com.sqx.modules.login.domain.vo.AccountVo;

/**
 * 账号管理服务
 */
public interface LoginAccountService {

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
     * @param id 账号ID
     */
    void delete(Long id);

    /**
     * 查询当前登录用户账号详情（App 端）
     *
     * @param accountId 当前账号ID
     * @return 账号详情
     */
    AccountVo currentDetail(Long accountId);
}
