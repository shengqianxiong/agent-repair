package com.sqx.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页响应
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<T> list;
    private long total;
    private int page;
    private int pageSize;

    public static <T> PageResult<T> of(List<T> list, long total, int page, int pageSize) {
        PageResult<T> pageResult = new PageResult<>();
        pageResult.setList(list == null ? Collections.emptyList() : list);
        pageResult.setTotal(total);
        pageResult.setPage(page);
        pageResult.setPageSize(pageSize);
        return pageResult;
    }
}
