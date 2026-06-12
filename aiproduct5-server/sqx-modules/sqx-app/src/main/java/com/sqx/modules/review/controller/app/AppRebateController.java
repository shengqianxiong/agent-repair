package com.sqx.modules.review.controller.app;

import com.sqx.common.Result;
import com.sqx.modules.review.domain.vo.RebateVo;
import com.sqx.modules.review.service.ReviewVerifyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/app/rebate")
public class AppRebateController {

    private final ReviewVerifyService verifyService;

    public AppRebateController(ReviewVerifyService verifyService) {
        this.verifyService = verifyService;
    }

    @GetMapping("/detail")
    public Result<RebateVo> detail(@RequestParam Long rebateId) {
        return Result.ok(verifyService.rebateDetail(rebateId));
    }
}
