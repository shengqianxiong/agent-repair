package com.sqx.modules.bar.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 酒吧桌位
 */
@Data
@TableName("bar_table")
public class BarTable implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String tableNo;
    private String area;
    private Integer capacity;
    /** 空闲/占用/预约中 */
    private String status;
    private Date createTime;
    private Date updateTime;
}
