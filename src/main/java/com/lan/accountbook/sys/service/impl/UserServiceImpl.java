package com.lan.accountbook.sys.service.impl;

import com.lan.accountbook.sys.domain.User;
import com.lan.accountbook.sys.mapper.UserMapper;
import com.lan.accountbook.sys.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
