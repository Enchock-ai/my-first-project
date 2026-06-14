package com.lan.accountbook.sys.service.impl;

import com.lan.accountbook.sys.domain.Billtype;
import com.lan.accountbook.sys.mapper.BilltypeMapper;
import com.lan.accountbook.sys.service.BilltypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.io.Serializable;

import org.springframework.stereotype.Service;


@Service
public class BilltypeServiceImpl extends ServiceImpl<BilltypeMapper, Billtype> implements BilltypeService {

	
	@Override
	public Billtype getById(Serializable id) {
		return super.getById(id);
	}
}
