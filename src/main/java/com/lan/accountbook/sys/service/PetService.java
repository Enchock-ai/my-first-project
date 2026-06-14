package com.lan.accountbook.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lan.accountbook.sys.domain.Pet;

public interface PetService extends IService<Pet> {
    Pet getByUserId(Integer userId);
    Pet adopt(Integer userId, String petType, String petName);
    boolean feed(Integer userId, int feedAmount);
    boolean dye(Integer userId, String color);
    boolean rename(Integer userId, String newName);
    boolean addCoins(Integer userId, int coins);
    boolean checkDailyLoginReward(Integer userId);
    int decreaseHungerForAll();

    // 商店相关
    boolean buyFood(Integer userId, String itemType, int quantity);
    boolean useFood(Integer userId, String itemType);
    java.util.Map<String, Integer> getUserItems(Integer userId);

    boolean play(Integer userId, int costCoins);
}