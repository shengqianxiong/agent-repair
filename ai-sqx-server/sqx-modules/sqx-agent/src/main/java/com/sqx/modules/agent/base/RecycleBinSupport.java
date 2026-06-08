package com.sqx.modules.agent.base;

import com.sqx.common.PageResult;
import com.sqx.common.exception.BusinessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 回收站通用操作（绕过 MyBatis-Plus 逻辑删除拦截）
 */
@Component
public class RecycleBinSupport {

    private final JdbcTemplate jdbcTemplate;

    public RecycleBinSupport(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> PageResult<T> page(String table, Class<T> clazz, int page, int pageSize) {
        String countSql = "SELECT COUNT(*) FROM " + table + " WHERE deleted = 1";
        Long total = jdbcTemplate.queryForObject(countSql, Long.class);
        long totalCount = total == null ? 0L : total;
        int offset = (page - 1) * pageSize;
        String sql = "SELECT * FROM " + table + " WHERE deleted = 1 ORDER BY delete_time DESC LIMIT ? OFFSET ?";
        List<T> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(clazz), pageSize, offset);
        return PageResult.of(list, totalCount, page, pageSize);
    }

    public void restore(String table, Long id) {
        int rows = jdbcTemplate.update(
                "UPDATE " + table + " SET deleted = 0, delete_time = NULL, update_time = NOW() WHERE id = ? AND deleted = 1",
                id);
        if (rows == 0) {
            throw new BusinessException("记录不存在或未在回收站");
        }
    }

    public void permanentDelete(String table, Long id) {
        int rows = jdbcTemplate.update("DELETE FROM " + table + " WHERE id = ? AND deleted = 1", id);
        if (rows == 0) {
            throw new BusinessException("记录不存在或未在回收站");
        }
    }
}
