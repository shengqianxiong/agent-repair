package com.sqx.modules.bar.domain.vo;

import lombok.Data;

@Data
public class UserVo {

    private Long id;
    private String nickname;
    private String avatar;
    private String phone;
    private Integer memberLevel;
    private String memberLevelName;
    private Integer points;
    private String token;
}
