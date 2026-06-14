package com.lan.accountbook.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lan.accountbook.sys.domain.Pet;
import org.apache.ibatis.annotations.Update;

public interface PetMapper extends BaseMapper<Pet> {

    // 每小时批量减少所有宠物的饥饿值，并同时更新心情
    @Update("UPDATE pet SET hunger = GREATEST(hunger - 2, 0), " +
            "mood = CASE " +
            "  WHEN GREATEST(hunger - 2, 0) <= 20 THEN 'sad' " +
            "  WHEN GREATEST(hunger - 2, 0) >= 80 THEN 'happy' " +
            "  ELSE mood " +
            "END")
    int decreaseHungerForAll();
}