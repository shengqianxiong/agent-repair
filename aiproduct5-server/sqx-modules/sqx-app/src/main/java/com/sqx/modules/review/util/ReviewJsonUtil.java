package com.sqx.modules.review.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.sqx.modules.review.domain.vo.CopywritingVo;
import com.sqx.modules.review.domain.vo.TaskVo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public final class ReviewJsonUtil {

    private ReviewJsonUtil() {
    }

    public static List<CopywritingVo> parseCopywriting(String json) {
        if (StrUtil.isBlank(json)) {
            return Collections.emptyList();
        }
        return JSONUtil.toList(JSONUtil.parseArray(json), CopywritingVo.class);
    }

    public static String toCopywritingJson(List<CopywritingVo> list) {
        if (list == null || list.isEmpty()) {
            return "[]";
        }
        return JSONUtil.toJsonStr(list);
    }

    public static List<String> parseImages(String json) {
        if (StrUtil.isBlank(json)) {
            return Collections.emptyList();
        }
        return JSONUtil.toList(JSONUtil.parseArray(json), String.class);
    }

    public static String toImagesJson(List<String> images) {
        if (images == null || images.isEmpty()) {
            return "[]";
        }
        return JSONUtil.toJsonStr(images);
    }

    public static String formatDateTime(Date date) {
        return date == null ? null : DateUtil.format(date, "yyyy-MM-dd HH:mm");
    }

    public static Date parseDateTime(String text) {
        if (StrUtil.isBlank(text)) {
            return null;
        }
        return DateUtil.parse(text);
    }

    public static String statusText(String status) {
        if (status == null) {
            return "";
        }
        switch (status) {
            case "started":
                return "进行中";
            case "auditing":
                return "审核中";
            case "paid":
                return "已到账";
            case "rejected":
                return "已拒绝";
            default:
                return status;
        }
    }

    public static void fillTaskStatus(TaskVo vo) {
        vo.setStatusText(statusText(vo.getStatus()));
    }

    public static List<CopywritingVo> defaultCopywriting(String productName) {
        List<CopywritingVo> list = new ArrayList<>();
        CopywritingVo first = new CopywritingVo();
        first.setId(1L);
        first.setTitle("真实体验版");
        first.setContent(productName + "品质不错，包装完整，整体体验超出预期，值得推荐。");
        list.add(first);
        return list;
    }
}
