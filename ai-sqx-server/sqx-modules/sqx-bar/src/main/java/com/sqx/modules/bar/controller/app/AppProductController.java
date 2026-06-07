package com.sqx.modules.bar.controller.app;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.common.annotation.Login;
import com.sqx.modules.bar.domain.bo.ProductQueryBo;
import com.sqx.modules.bar.domain.vo.ProductVo;
import com.sqx.modules.bar.service.BarProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端 - 酒水商品
 */
@RestController
@RequestMapping("/app/product")
public class AppProductController {

    private final BarProductService productService;

    public AppProductController(BarProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/list")
    @Login
    public Result<PageResult<ProductVo>> list(ProductQueryBo queryBo) {
        if (queryBo.getStatus() == null) {
            queryBo.setStatus(1);
        }
        return Result.ok(productService.page(queryBo));
    }

    @GetMapping("/detail/{id}")
    @Login
    public Result<ProductVo> detail(@PathVariable Long id) {
        ProductVo productVo = productService.detail(id);
        if (productVo.getStatus() != null && productVo.getStatus() == 0) {
            return Result.error("商品已下架");
        }
        return Result.ok(productVo);
    }
}
