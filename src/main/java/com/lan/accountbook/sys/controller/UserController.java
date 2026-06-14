package com.lan.accountbook.sys.controller;

import com.lan.accountbook.common.Result;
import com.lan.accountbook.sys.domain.User;
import com.lan.accountbook.sys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 获取当前登录用户信息
    @GetMapping("/profile")
    public Result getUserProfile(HttpSession session) {
        User loginUser = (User) session.getAttribute("user");
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        User user = userService.getById(loginUser.getId());
        Map<String, Object> data = new HashMap<>();
        data.put("nickname", user.getNickname());
        data.put("avatarUrl", user.getAvatar());
        data.put("backgroundUrl", user.getBackground());
        return Result.success(data);
    }

    // 修改昵称
    @PutMapping("/profile")
    public Result updateNickname(@RequestBody Map<String, String> params, HttpSession session) {
        User loginUser = (User) session.getAttribute("user");
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        String newNickname = params.get("nickname");
        if (newNickname == null || newNickname.trim().isEmpty()) {
            return Result.error(400, "昵称不能为空");
        }
        loginUser.setNickname(newNickname.trim());
        userService.updateById(loginUser);
        session.setAttribute("user", loginUser);
        return Result.success("修改昵称成功");
    }

    // 上传头像（Base64）
    @PostMapping("/avatar")
    public Result uploadAvatar(@RequestParam("avatar") MultipartFile file, HttpSession session) {
        User loginUser = (User) session.getAttribute("user");
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        try {
            String base64 = "data:" + file.getContentType() + ";base64," +
                    Base64.getEncoder().encodeToString(file.getBytes());
            loginUser.setAvatar(base64);
            userService.updateById(loginUser);
            session.setAttribute("user", loginUser);
            Map<String, Object> data = new HashMap<>();
            data.put("avatarUrl", base64);
            return Result.success(data);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("头像上传失败");
        }
    }

    // 上传背景图（Base64）
    @PostMapping("/background")
    public Result uploadBackground(@RequestParam("background") MultipartFile file, HttpSession session) {
        User loginUser = (User) session.getAttribute("user");
        if (loginUser == null) {
            return Result.error(401, "未登录");
        }
        try {
            String base64 = "data:" + file.getContentType() + ";base64," +
                    Base64.getEncoder().encodeToString(file.getBytes());
            loginUser.setBackground(base64);
            userService.updateById(loginUser);
            session.setAttribute("user", loginUser);
            Map<String, Object> data = new HashMap<>();
            data.put("backgroundUrl", base64);
            return Result.success(data);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("背景图上传失败");
        }
    }


    @GetMapping("/checkLogin")
    @ResponseBody
    public Result checkLogin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return Result.success("已登录");
        } else {
            return Result.error(401, "未登录");
        }
    }

}