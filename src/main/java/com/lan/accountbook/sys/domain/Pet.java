package com.lan.accountbook.sys.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("pet")
public class Pet {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String petType;       // cat, dog, rabbit
    private String petName;
    private String color;         // 染色颜色值，如 #FFB6C1
    private Integer hunger;       // 0-100
    private Integer coins;
    private String avatarUrl;     // 宠物图片URL
    private String mood;          // happy, sad, excited, calm
    private Date lastLoginDate;   // 上次登录日期（用于每日奖励）
    private Integer consecutiveDays; // 连续记账天数
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
}