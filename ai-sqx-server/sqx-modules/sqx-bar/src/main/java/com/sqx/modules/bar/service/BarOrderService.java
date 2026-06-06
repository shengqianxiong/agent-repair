package com.sqx.modules.bar.service;

import com.sqx.common.PageResult;
import com.sqx.modules.bar.domain.bo.OrderPayBo;
import com.sqx.modules.bar.domain.bo.OrderQueryBo;
import com.sqx.modules.bar.domain.bo.OrderStatusBo;
import com.sqx.modules.bar.domain.bo.OrderSubmitBo;
import com.sqx.modules.bar.domain.vo.OrderVo;
import com.sqx.modules.bar.domain.vo.PayVo;

import java.util.List;

public interface BarOrderService {

    OrderVo submit(Long userId, OrderSubmitBo bo);

    PayVo pay(Long userId, OrderPayBo bo);

    void confirmPay(Long orderId);

    PageResult<OrderVo> page(OrderQueryBo queryBo);

    List<OrderVo> recent(int limit);

    OrderVo detail(Long id);

    void cancel(Long userId, Long id);

    void updateStatus(OrderStatusBo bo);

    void refund(Long id);
}
