package com.sqx.modules.review.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("review_activity")
public class ReviewActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;
    private String code;
    private String title;
    private String productName;
    private String productImage;
    private String shopName;
    private String productUrl;
    private BigDecimal rebateAmount;
    private Date validEndTime;
    private String douyinLink;
    private String copywritingJson;
    private String recommendImagesJson;
    /** 0-下架 1-上架 */
    private Integer status;
    private Integer scanCount;
    private Integer submitCount;
    private Integer verifyCount;
    private Date createTime;
    private Date updateTime;
}
