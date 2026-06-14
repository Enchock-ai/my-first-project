package com.lan.accountbook.sys.controller;

import com.lan.accountbook.common.Result;
import com.lan.accountbook.sys.domain.Pet;
import com.lan.accountbook.sys.domain.User;
import com.lan.accountbook.sys.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pet")
public class PetController {

    @Autowired
    private PetService petService;

    // 获取当前用户的宠物信息
    @GetMapping("/info")
    public Result getPetInfo(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "未登录");
        Pet pet = petService.getByUserId(user.getId());
        if (pet == null) return Result.success(null);
        Map<String, Object> data = new HashMap<>();
        data.put("id", pet.getId());
        data.put("petType", pet.getPetType());
        data.put("petName", pet.getPetName());
        data.put("color", pet.getColor());
        data.put("hunger", pet.getHunger());
        data.put("coins", pet.getCoins());
        data.put("avatarUrl", pet.getAvatarUrl());
        data.put("mood", pet.getMood());
        return Result.success(data);
    }


    // 领养宠物
    @PostMapping("/adopt")
    public Result adopt(@RequestParam String petType, @RequestParam(required = false) String petName, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        try {
            petService.adopt(user.getId(), petType, petName);
            return Result.success("领养成功！");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 喂食（直接扣币）
    @PostMapping("/feed")
    public Result feed(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        try {
            boolean ok = petService.feed(user.getId(), 10);
            return ok ? Result.success("喂食成功，饱腹+10") : Result.error("喂食失败");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/play")
    @ResponseBody
    public Result play(HttpSession session) {
        User loginUser = (User) session.getAttribute("user");
        if (loginUser == null) return Result.error(401, "未登录");
        try {
            boolean ok = petService.play(loginUser.getId(), 10);
            return ok ? Result.success("玩耍成功") : Result.error("萌宠币不足");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }

    // 改名
    @PostMapping("/rename")
    public Result rename(@RequestParam String newName, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "请先登录");
        boolean ok = petService.rename(user.getId(), newName);
        return ok ? Result.success("改名成功") : Result.error("改名失败");
    }

    // 获取用户当前宠物币（方便前端）
    @GetMapping("/coins")
    public Result getCoins(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "未登录");
        Pet pet = petService.getByUserId(user.getId());
        return Result.success(pet == null ? 0 : pet.getCoins());
    }

    // ================== 商店接口 ==================
    @GetMapping("/items")
    public Result getUserItems(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "未登录");
        return Result.success(petService.getUserItems(user.getId()));
    }

    @PostMapping("/shop/buy")
    public Result buyFood(@RequestParam String itemType, @RequestParam int quantity, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "未登录");
        boolean ok = petService.buyFood(user.getId(), itemType, quantity);
        return ok ? Result.success("购买成功") : Result.error("萌宠币不足或购买失败");
    }

    @PostMapping("/shop/use")
    public Result useFood(@RequestParam String itemType, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) return Result.error(401, "未登录");
        boolean ok = petService.useFood(user.getId(), itemType);
        return ok ? Result.success("使用成功") : Result.error("使用失败，可能没有该食物");
    }
}