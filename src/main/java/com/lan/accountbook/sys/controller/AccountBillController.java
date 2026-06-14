package com.lan.accountbook.sys.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import com.lan.accountbook.sys.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lan.accountbook.common.DataGridView;
import com.lan.accountbook.common.ResultObj;
import com.lan.accountbook.sys.domain.Bills;
import com.lan.accountbook.sys.domain.Billtype;
import com.lan.accountbook.sys.domain.User;
import com.lan.accountbook.sys.service.AccountBillService;
import com.lan.accountbook.sys.service.BilltypeService;
import com.lan.accountbook.sys.vo.AccountBillVo;


@Controller
@RequestMapping("/bills")
public class AccountBillController {

	@Autowired
	private AccountBillService billService;

	@Autowired
	private BilltypeService billTypeService;

	@Autowired
	private PetService petService;


	@RequestMapping("toBillsList")
	public String toBillsList() {
		return "list";
	}

	@RequestMapping("loadAllBills")
	@ResponseBody
	public DataGridView loadAllBills(AccountBillVo accountBillVo, HttpSession session) {
		User loginUser = (User) session.getAttribute("user");
		if (loginUser == null) {
			return new DataGridView(0L, new ArrayList<>());
		}

		IPage<Bills> page = new Page<>(accountBillVo.getPage(), accountBillVo.getLimit());
		QueryWrapper<Bills> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("user_id", loginUser.getId());
		queryWrapper.eq(null != accountBillVo.getTypeid() && accountBillVo.getTypeid() != 0, "typeid", accountBillVo.getTypeid());
		queryWrapper.ge(accountBillVo.getStartDate() != null, "billtime", accountBillVo.getStartDate());
		queryWrapper.le(accountBillVo.getEndDate() != null, "billtime", accountBillVo.getEndDate());
		queryWrapper.orderByAsc("billtime");

		billService.page(page, queryWrapper);
		List<Bills> records = page.getRecords();
		for (Bills bills : records) {
			if (bills.getTypeid() != null) {
				Billtype billtype = this.billTypeService.getById(bills.getTypeid());
				bills.setTypeName(billtype != null ? billtype.getName() : "未分类");
			} else {
				bills.setTypeName("未分类");
			}
		}
		return new DataGridView(page.getTotal(), records);
	}

	@RequestMapping("addBills")
	@ResponseBody
	public ResultObj addBills(AccountBillVo accountBillVo, HttpSession session) {
		User loginUser = (User) session.getAttribute("user");
		if (loginUser == null) {
			return new ResultObj(-1, "请先登录");
		}
		accountBillVo.setUserId(loginUser.getId()); // 实体字段 userId
		try {
			this.billService.save(accountBillVo);
			petService.addCoins(loginUser.getId(), 20);
			return new ResultObj(200, "录入成功,增加20萌宠币");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultObj(-1, "录入失败");
		}
	}

	@RequestMapping("updateBills")
	@ResponseBody
	public ResultObj updateBills(AccountBillVo accountBillVo, HttpSession session) {
		User loginUser = (User) session.getAttribute("user");
		if (loginUser == null) {
			return new ResultObj(-1, "请先登录");
		}
		Bills oldBill = billService.getById(accountBillVo.getId());
		if (oldBill == null || !oldBill.getUserId().equals(loginUser.getId())) {
			return new ResultObj(-1, "无权修改此账单");
		}
		accountBillVo.setUserId(null); // 防止篡改
		try {
			this.billService.updateById(accountBillVo);
			return new ResultObj(200, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultObj(-1, "修改失败");
		}
	}

	@RequestMapping("deleteBills")
	@ResponseBody
	public ResultObj deleteBills(Integer id, HttpSession session) {
		User loginUser = (User) session.getAttribute("user");
		if (loginUser == null) {
			return new ResultObj(-1, "请先登录");
		}
		Bills bill = billService.getById(id);
		if (bill == null || !bill.getUserId().equals(loginUser.getId())) {
			return new ResultObj(-1, "无权删除此账单");
		}
		try {
			this.billService.removeById(id);
			return new ResultObj(200, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new ResultObj(-1, "删除失败");
		}
	}
}