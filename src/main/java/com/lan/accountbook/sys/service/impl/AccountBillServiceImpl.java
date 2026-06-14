package com.lan.accountbook.sys.service.impl;

import com.lan.accountbook.sys.domain.Bills;
import com.lan.accountbook.sys.mapper.AccountBillMapper;
import com.lan.accountbook.sys.service.AccountBillService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class AccountBillServiceImpl extends ServiceImpl<AccountBillMapper, Bills> implements AccountBillService {

}
