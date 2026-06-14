package com.lan.accountbook.sys.domain;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class User implements Serializable {

    private static final long serialVersionUID=1L;

    private Integer id;

    private String username;

    private String loginname;

    private String pwd;

    private String sex;

    // ============ 新增的 3 个字段 ============
    // 用户昵称
    private String nickname;

    // 用户头像（Base64字符串）
    private String avatar;

    // 用户背景图（Base64字符串）
    private String background;


}
