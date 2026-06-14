package com.lan.accountbook.sys.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.lan.accountbook.sys.domain.Bills;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class AccountBillVo extends Bills{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date startDate;
	@DateTimeFormat(pattern="yyyy-MM-dd")
	private Date endDate;
	
	private Integer page=1;
	private Integer limit=10;

}
