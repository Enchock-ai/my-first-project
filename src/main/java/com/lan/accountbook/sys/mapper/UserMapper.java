package com.lan.accountbook.sys.mapper;

import com.lan.accountbook.sys.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

@Mapper // 加上这个注解
public interface UserMapper extends BaseMapper<User> {
    User selectUserByUsername(@Param("username") String username);
}

