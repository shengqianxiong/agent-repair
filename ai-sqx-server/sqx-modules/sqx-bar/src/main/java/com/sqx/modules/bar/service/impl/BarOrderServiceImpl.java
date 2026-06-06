package com.sqx.modules.bar.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.bar.constant.BarConstants;
import com.sqx.modules.bar.db.entity.BarOrder;
import com.sqx.modules.bar.db.entity.BarOrderItem;
import com.sqx.modules.bar.db.entity.BarProduct;
import com.sqx.modules.bar.db.entity.BarTable;
import com.sqx.modules.bar.db.entity.BarUser;
import com.sqx.modules.bar.db.mapper.BarOrderItemMapper;
import com.sqx.modules.bar.db.mapper.BarOrderMapper;
import com.sqx.modules.bar.db.mapper.BarProductMapper;
import com.sqx.modules.bar.db.mapper.BarTableMapper;
import com.sqx.modules.bar.db.mapper.BarUserMapper;
import com.sqx.modules.bar.domain.bo.OrderPayBo;
import com.sqx.modules.bar.domain.bo.OrderQueryBo;
import com.sqx.modules.bar.domain.bo.OrderStatusBo;
import com.sqx.modules.bar.domain.bo.OrderSubmitBo;
import com.sqx.modules.bar.domain.vo.CartVo;
import com.sqx.modules.bar.domain.vo.OrderItemVo;
import com.sqx.modules.bar.domain.vo.OrderVo;
import com.sqx.modules.bar.domain.vo.PayVo;
import com.sqx.modules.bar.service.BarCartService;
import com.sqx.modules.bar.service.BarOrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BarOrderServiceImpl implements BarOrderService {

    private final BarOrderMapper orderMapper;
    private final BarOrderItemMapper orderItemMapper;
    private final BarCartService cartService;
    private final BarProductMapper productMapper;
    private final BarTableMapper tableMapper;
    private final BarUserMapper userMapper;

    public BarOrderServiceImpl(BarOrderMapper orderMapper,
                               BarOrderItemMapper orderItemMapper,
                               BarCartService cartService,
                               BarProductMapper productMapper,
                               BarTableMapper tableMapper,
                               BarUserMapper userMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.cartService = cartService;
        this.productMapper = productMapper;
        this.tableMapper = tableMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVo submit(Long userId, OrderSubmitBo bo) {
        List<CartVo> cartList = cartService.list(userId);
        if (cartList.isEmpty()) {
            throw new BusinessException("购物车为空");
        }
        if (bo.getTableId() != null) {
            BarTable table = tableMapper.selectById(bo.getTableId());
            if (table == null) {
                throw new BusinessException("桌位不存在");
            }
        }
        BigDecimal totalAmount = cartList.stream()
                .map(CartVo::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Date now = new Date();
        BarOrder order = new BarOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setTableId(bo.getTableId());
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setStatus(BarConstants.ORDER_STATUS_PENDING);
        order.setRemark(bo.getRemark());
        order.setCreateTime(now);
        orderMapper.insert(order);

        for (CartVo cart : cartList) {
            BarOrderItem item = new BarOrderItem();
            item.setOrderId(order.getId());
            item.setProductId(cart.getProductId());
            item.setProductName(cart.getProductName());
            item.setQuantity(cart.getQuantity());
            item.setUnitPrice(cart.getPrice());
            item.setSubtotal(cart.getSubtotal());
            orderItemMapper.insert(item);
            BarProduct product = productMapper.selectById(cart.getProductId());
            if (product != null && product.getStock() != null) {
                product.setStock(Math.max(0, product.getStock() - cart.getQuantity()));
                productMapper.updateById(product);
            }
        }
        cartService.clear(userId);
        return detail(order.getId());
    }

    @Override
    public PayVo pay(Long userId, OrderPayBo bo) {
        if (bo.getOrderId() == null) {
            throw new BusinessException("订单ID不能为空");
        }
        BarOrder order = getOrderOrThrow(bo.getOrderId());
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (!BarConstants.ORDER_STATUS_PENDING.equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许支付");
        }
        PayVo payVo = new PayVo();
        payVo.setOrderId(order.getId());
        payVo.setOrderNo(order.getOrderNo());
        payVo.setTimeStamp(String.valueOf(System.currentTimeMillis() / 1000));
        payVo.setNonceStr("nonce_" + order.getId());
        payVo.setPackageValue("prepay_id=mock_" + order.getOrderNo());
        payVo.setPaySign("mock_sign_" + order.getOrderNo());
        return payVo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmPay(Long orderId) {
        BarOrder order = getOrderOrThrow(orderId);
        if (!BarConstants.ORDER_STATUS_PENDING.equals(order.getStatus())) {
            throw new BusinessException("订单状态不允许支付");
        }
        order.setStatus(BarConstants.ORDER_STATUS_PAID);
        order.setPayTime(new Date());
        orderMapper.updateById(order);
        if (order.getTableId() != null) {
            BarTable table = tableMapper.selectById(order.getTableId());
            if (table != null) {
                table.setStatus(BarConstants.TABLE_STATUS_OCCUPIED);
                table.setUpdateTime(new Date());
                tableMapper.updateById(table);
            }
        }
        BarUser user = userMapper.selectById(order.getUserId());
        if (user != null) {
            int points = order.getPayAmount().intValue();
            user.setPoints((user.getPoints() == null ? 0 : user.getPoints()) + points);
            user.setUpdateTime(new Date());
            userMapper.updateById(user);
        }
    }

    @Override
    public PageResult<OrderVo> page(OrderQueryBo queryBo) {
        int page = queryBo.getPage() == null || queryBo.getPage() < 1 ? 1 : queryBo.getPage();
        int pageSize = queryBo.getPageSize() == null || queryBo.getPageSize() < 1 ? 10 : queryBo.getPageSize();
        LambdaQueryWrapper<BarOrder> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(queryBo.getStatus())) {
            wrapper.eq(BarOrder::getStatus, queryBo.getStatus());
        }
        if (StrUtil.isNotBlank(queryBo.getOrderNo())) {
            wrapper.like(BarOrder::getOrderNo, queryBo.getOrderNo());
        }
        if (queryBo.getUserId() != null) {
            wrapper.eq(BarOrder::getUserId, queryBo.getUserId());
        }
        wrapper.orderByDesc(BarOrder::getCreateTime);
        Page<BarOrder> pageData = orderMapper.selectPage(new Page<>(page, pageSize), wrapper);
        List<OrderVo> list = toVoList(pageData.getRecords(), true);
        return PageResult.of(list, pageData.getTotal(), page, pageSize);
    }

    @Override
    public List<OrderVo> recent(int limit) {
        List<BarOrder> orders = orderMapper.selectList(new LambdaQueryWrapper<BarOrder>()
                .orderByDesc(BarOrder::getCreateTime)
                .last("LIMIT " + limit));
        return toVoList(orders, true);
    }

    @Override
    public OrderVo detail(Long id) {
        BarOrder order = getOrderOrThrow(id);
        return toVo(order, true);
    }

    @Override
    public void cancel(Long userId, Long id) {
        BarOrder order = getOrderOrThrow(id);
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (!BarConstants.ORDER_STATUS_PENDING.equals(order.getStatus())) {
            throw new BusinessException("仅待支付订单可取消");
        }
        order.setStatus(BarConstants.ORDER_STATUS_CANCELLED);
        orderMapper.updateById(order);
    }

    @Override
    public void updateStatus(OrderStatusBo bo) {
        if (bo.getId() == null || StrUtil.isBlank(bo.getStatus())) {
            throw new BusinessException("参数不完整");
        }
        BarOrder order = getOrderOrThrow(bo.getId());
        order.setStatus(bo.getStatus());
        orderMapper.updateById(order);
        if (BarConstants.ORDER_STATUS_COMPLETED.equals(bo.getStatus()) && order.getTableId() != null) {
            BarTable table = tableMapper.selectById(order.getTableId());
            if (table != null) {
                table.setStatus(BarConstants.TABLE_STATUS_FREE);
                table.setUpdateTime(new Date());
                tableMapper.updateById(table);
            }
        }
    }

    @Override
    public void refund(Long id) {
        BarOrder order = getOrderOrThrow(id);
        if (!BarConstants.ORDER_STATUS_PAID.equals(order.getStatus())
                && !BarConstants.ORDER_STATUS_PREPARING.equals(order.getStatus())) {
            throw new BusinessException("当前订单状态不支持退款");
        }
        order.setStatus(BarConstants.ORDER_STATUS_CANCELLED);
        orderMapper.updateById(order);
    }

    private String generateOrderNo() {
        return "BO" + System.currentTimeMillis();
    }

    private BarOrder getOrderOrThrow(Long id) {
        BarOrder order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return order;
    }

    private List<OrderVo> toVoList(List<BarOrder> orders, boolean withItems) {
        if (orders.isEmpty()) {
            return new ArrayList<>();
        }
        Map<Long, String> userMap = userMapper.selectBatchIds(
                orders.stream().map(BarOrder::getUserId).distinct().collect(Collectors.toList())
        ).stream().collect(Collectors.toMap(BarUser::getId, BarUser::getNickname, (a, b) -> a));
        Map<Long, String> tableMap = tableMapper.selectList(null).stream()
                .collect(Collectors.toMap(BarTable::getId, BarTable::getTableNo, (a, b) -> a));
        return orders.stream().map(order -> {
            OrderVo vo = new OrderVo();
            BeanUtil.copyProperties(order, vo);
            vo.setUserNickname(userMap.get(order.getUserId()));
            if (order.getTableId() != null) {
                vo.setTableNo(tableMap.get(order.getTableId()));
            }
            if (withItems) {
                vo.setItems(loadItems(order.getId()));
            }
            return vo;
        }).collect(Collectors.toList());
    }

    private OrderVo toVo(BarOrder order, boolean withItems) {
        OrderVo vo = new OrderVo();
        BeanUtil.copyProperties(order, vo);
        BarUser user = userMapper.selectById(order.getUserId());
        if (user != null) {
            vo.setUserNickname(user.getNickname());
        }
        if (order.getTableId() != null) {
            BarTable table = tableMapper.selectById(order.getTableId());
            if (table != null) {
                vo.setTableNo(table.getTableNo());
            }
        }
        if (withItems) {
            vo.setItems(loadItems(order.getId()));
        }
        return vo;
    }

    private List<OrderItemVo> loadItems(Long orderId) {
        return orderItemMapper.selectList(new LambdaQueryWrapper<BarOrderItem>()
                        .eq(BarOrderItem::getOrderId, orderId))
                .stream().map(item -> {
                    OrderItemVo vo = new OrderItemVo();
                    BeanUtil.copyProperties(item, vo);
                    return vo;
                }).collect(Collectors.toList());
    }
}
