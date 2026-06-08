package com.sqx.modules.agent.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sqx.modules.agent.db.entity.AgentDelivery;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

@Mapper
public interface AgentDeliveryMapper extends BaseMapper<AgentDelivery> {

    @Select("SELECT * FROM agent_delivery WHERE id = #{id}")
    AgentDelivery selectByIdIncludeDeleted(@Param("id") Long id);

    @Select("SELECT * FROM agent_delivery WHERE deleted = 1 ORDER BY deleted_time DESC")
    Page<AgentDelivery> selectRecyclePage(Page<AgentDelivery> page);

    @Update("UPDATE agent_delivery SET deleted = 0, deleted_time = NULL, update_time = #{updateTime} WHERE id = #{id} AND deleted = 1")
    int restoreById(@Param("id") Long id, @Param("updateTime") Date updateTime);

    @Update("UPDATE agent_delivery SET deleted = 1, deleted_time = #{deletedTime}, update_time = #{deletedTime} WHERE project_id = #{projectId} AND deleted = 0")
    int softDeleteByProjectId(@Param("projectId") Long projectId, @Param("deletedTime") Date deletedTime);

    @Update("UPDATE agent_delivery SET deleted = 1, deleted_time = #{deletedTime}, update_time = #{deletedTime} WHERE customer_id = #{customerId} AND deleted = 0")
    int softDeleteByCustomerId(@Param("customerId") Long customerId, @Param("deletedTime") Date deletedTime);

    @Update("UPDATE agent_delivery SET deleted = 1, deleted_time = #{deletedTime}, update_time = #{deletedTime} WHERE id = #{id} AND deleted = 0")
    int softDeleteById(@Param("id") Long id, @Param("deletedTime") Date deletedTime);

    @Delete("DELETE FROM agent_delivery WHERE id = #{id} AND deleted = 1")
    int permanentDeleteById(@Param("id") Long id);
}
