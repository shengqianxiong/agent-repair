package com.sqx.modules.bar.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sqx.common.exception.BusinessException;
import com.sqx.modules.bar.db.entity.BarCart;
import com.sqx.modules.bar.db.entity.BarProduct;
import com.sqx.modules.bar.db.mapper.BarCartMapper;
import com.sqx.modules.bar.db.mapper.BarProductMapper;
import com.sqx.modules.bar.domain.bo.CartAddBo;
import com.sqx.modules.bar.domain.bo.CartUpdateBo;
import com.sqx.modules.bar.domain.vo.CartVo;
import com.sqx.modules.bar.service.BarCartService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BarCartServiceImpl implements BarCartService {

    private final BarCartMapper cartMapper;
    private final BarProductMapper productMapper;

    public BarCartServiceImpl(BarCartMapper cartMapper, BarProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    @Override
    public List<CartVo> list(Long userId) {
        List<BarCart> carts = cartMapper.selectList(new LambdaQueryWrapper<BarCart>()
                .eq(BarCart::getUserId, userId)
                .orderByDesc(BarCart::getUpdateTime));
        if (carts.isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> productIds = carts.stream().map(BarCart::getProductId).collect(Collectors.toList());
        Map<Long, BarProduct> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(BarProduct::getId, p -> p));
        List<CartVo> result = new ArrayList<>();
        for (BarCart cart : carts) {
            BarProduct product = productMap.get(cart.getProductId());
            if (product == null || product.getStatus() == 0) {
                continue;
            }
            CartVo vo = new CartVo();
            vo.setId(cart.getId());
            vo.setProductId(product.getId());
            vo.setProductName(product.getName());
            vo.setImage(product.getImage());
            vo.setPrice(product.getPrice());
            vo.setQuantity(cart.getQuantity());
            vo.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())));
            result.add(vo);
        }
        return result;
    }

    @Override
    public void add(Long userId, CartAddBo bo) {
        if (bo.getProductId() == null) {
            throw new BusinessException("商品ID不能为空");
        }
        int quantity = bo.getQuantity() == null || bo.getQuantity() < 1 ? 1 : bo.getQuantity();
        BarProduct product = productMapper.selectById(bo.getProductId());
        if (product == null || product.getStatus() == 0) {
            throw new BusinessException("商品不存在或已下架");
        }
        BarCart exist = cartMapper.selectOne(new LambdaQueryWrapper<BarCart>()
                .eq(BarCart::getUserId, userId)
                .eq(BarCart::getProductId, bo.getProductId()));
        Date now = new Date();
        if (exist != null) {
            exist.setQuantity(exist.getQuantity() + quantity);
            exist.setUpdateTime(now);
            cartMapper.updateById(exist);
        } else {
            BarCart cart = new BarCart();
            cart.setUserId(userId);
            cart.setProductId(bo.getProductId());
            cart.setQuantity(quantity);
            cart.setCreateTime(now);
            cart.setUpdateTime(now);
            cartMapper.insert(cart);
        }
    }

    @Override
    public void update(Long userId, CartUpdateBo bo) {
        if (bo.getId() == null) {
            throw new BusinessException("购物车ID不能为空");
        }
        if (bo.getQuantity() == null || bo.getQuantity() < 1) {
            throw new BusinessException("数量不能小于1");
        }
        BarCart cart = getCartOrThrow(userId, bo.getId());
        cart.setQuantity(bo.getQuantity());
        cart.setUpdateTime(new Date());
        cartMapper.updateById(cart);
    }

    @Override
    public void remove(Long userId, Long id) {
        getCartOrThrow(userId, id);
        cartMapper.deleteById(id);
    }

    @Override
    public void clear(Long userId) {
        cartMapper.delete(new LambdaQueryWrapper<BarCart>().eq(BarCart::getUserId, userId));
    }

    private BarCart getCartOrThrow(Long userId, Long id) {
        BarCart cart = cartMapper.selectById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("购物车记录不存在");
        }
        return cart;
    }
}
