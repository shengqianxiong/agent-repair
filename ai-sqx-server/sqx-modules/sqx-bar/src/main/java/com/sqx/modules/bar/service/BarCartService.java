package com.sqx.modules.bar.service;

import com.sqx.modules.bar.domain.bo.CartAddBo;
import com.sqx.modules.bar.domain.bo.CartUpdateBo;
import com.sqx.modules.bar.domain.vo.CartVo;

import java.util.List;

public interface BarCartService {

    List<CartVo> list(Long userId);

    void add(Long userId, CartAddBo bo);

    void update(Long userId, CartUpdateBo bo);

    void remove(Long userId, Long id);

    void clear(Long userId);
}
