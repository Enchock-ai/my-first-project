package com.lan.accountbook.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultObj {
	private int code;
	private String msg;
	private Object data;   // 新增：用于存放额外数据

	// 保留原有的两参数构造器，方便登录等场景使用
	public ResultObj(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

}