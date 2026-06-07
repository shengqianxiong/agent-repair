package com.sqx.modules.bar.domain.bo;

import lombok.Data;

@Data
public class ProductQueryBo {

    private String name;
    private Long categoryId;
    private Integer status;
    /** 分页页码（与前端 pageNum 对齐） */
    private Integer pageNum;
    private Integer page = 1;
    private Integer pageSize = 10;

    /**
     * 获取有效页码，优先 pageNum
     */
    public int resolvePage() {
        if (pageNum != null && pageNum > 0) {
            return pageNum;
        }
        return page == null || page < 1 ? 1 : page;
    }
}
