package com.lan.accountbook.sys.controller;

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.lan.accountbook.common.Result;
import com.lan.accountbook.sys.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lan.accountbook.common.ResultObj;
import com.lan.accountbook.sys.domain.User;
import com.lan.accountbook.sys.mapper.UserMapper;
import com.lan.accountbook.sys.service.UserService;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;

@Controller
public class LoginController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private PetService petService;

	// 首页：localhost:8080 直接进登录页
	@GetMapping("/")
	public String index() {
		return "index";
	}


	@RequestMapping("/guest/bills")
	public String guestBills() {
		return "guest_bills";
	}

	@RequestMapping("/guest/pet")
	public String guestPet() {
		return "guest_pet";
	}

	@RequestMapping("/guest/calc")
	public String guestCalc() {
		return "guest_calc";
	}

	@RequestMapping("/login/toLogin")
	public String toLogin() {
		return "login";
	}

	@RequestMapping("/login/login")
	@ResponseBody
	public ResultObj login(String loginname,String pwd,String code,HttpSession session) {
		Object codeSession = session.getAttribute("code");
		session.removeAttribute("code");
		if(code!=null&&code.equals(codeSession)) {

			QueryWrapper<User> queryWrapper=new QueryWrapper<>();
			queryWrapper.eq("loginname", loginname);
			queryWrapper.eq("pwd", pwd);
			User user = userService.getOne(queryWrapper);
			if(null!=user) {
				session.setAttribute("user", user);
				return new ResultObj(200, "登陆成功");
			}else {
				return new ResultObj(-1, "用户名或密码不正确");
			}
		}else {
			return new ResultObj(-1, "验证码错误");
		}
	}

	@RequestMapping("/login/getCode")
	public void getCode(HttpServletResponse resp,HttpSession session) throws IOException {
		CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(116, 36, 4, 5);
		String code = captcha.getCode();
		session.setAttribute("code", code);
		ServletOutputStream outputStream = resp.getOutputStream();
		captcha.write(outputStream);
		outputStream.close();
	}

	// 去注册页
	@GetMapping("/login/register")
	public String register() {
		return "register";
	}

	// 处理注册提交
	@PostMapping("/login/register")
	public String doRegister(User user, Model model) {
		QueryWrapper<User> wrapper = new QueryWrapper<>();
		wrapper.eq("loginname", user.getUsername());
		User exist = userMapper.selectOne(wrapper);

		if (exist != null) {
			model.addAttribute("msg", "用户名已存在");
			return "register";
		}

		userMapper.insert(user);
		model.addAttribute("msg", "注册成功，请登录");
		return "login";

	}
}