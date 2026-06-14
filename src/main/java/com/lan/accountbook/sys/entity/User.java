package com.lan.accountbook.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

// 对应你数据库里的 user 表，表名如果不一样要改这里
@TableName("user")
@Data // 自动生成getter、setter等方法，非常方便
public class User {
    // 主键自增
    @TableId(type = IdType.AUTO)
    private Integer id;

    // 这些字段要和你数据库 user 表的字段一一对应
    private String username;
    private String password;
    // 如果你表里面还有 phone、email 这些字段，也可以加进来
    // private String phone;
}