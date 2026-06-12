package com.sqx.modules.review.controller.admin;

import com.sqx.common.PageResult;
import com.sqx.common.Result;
import com.sqx.modules.review.domain.bo.VerifyActionBo;
import com.sqx.modules.review.domain.bo.VerifyQueryBo;
import com.sqx.modules.review.domain.vo.TaskVo;
import com.sqx.modules.review.service.ReviewVerifyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/verify")
public class AdminVerifyController {

    private final ReviewVerifyService verifyService;

    public AdminVerifyController(ReviewVerifyService verifyService) {
        this.verifyService = verifyService;
    }

    @GetMapping("/list")
    public Result<PageResult<TaskVo>> list(VerifyQueryBo queryBo) {
        return Result.ok(verifyService.page(queryBo));
    }

    @PostMapping("/pass")
    public Result<Void> pass(@RequestBody VerifyActionBo bo) {
        verifyService.pass(bo);
        return Result.ok();
    }

    @PostMapping("/reject")
    public Result<Void> reject(@RequestBody VerifyActionBo bo) {
        verifyService.reject(bo);
        return Result.ok();
    }
}
