package com.lan.accountbook.sys.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("pet_item")
public class PetItem {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String itemType;   // food_small, food_medium, food_large
    private Integer quantity;
}