package com.lan.accountbook.sys.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.lan.accountbook.common.DataGridView;
import com.lan.accountbook.sys.service.BilltypeService;


@RestController
@RequestMapping("/billtype")
public class BilltypeController {
	
	@Autowired
	private BilltypeService billtypeService;

	
	@RequestMapping("loadAllBillType")
	public DataGridView loadAllBillType() {
		return new DataGridView(0L, billtypeService.list());
	}

}

