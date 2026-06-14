package com.lan.accountbook.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lan.accountbook.sys.domain.Pet;
import com.lan.accountbook.sys.domain.PetItem;
import com.lan.accountbook.sys.mapper.PetItemMapper;
import com.lan.accountbook.sys.mapper.PetMapper;
import com.lan.accountbook.sys.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PetServiceImpl extends ServiceImpl<PetMapper, Pet> implements PetService {

    @Autowired
    private PetMapper petMapper;

    @Autowired
    private PetItemMapper petItemMapper;  // 后续创建

    // ================== 基础功能 ==================

    @Override
    public Pet getByUserId(Integer userId) {
        LambdaQueryWrapper<Pet> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Pet::getUserId, userId);
        return getOne(wrapper);
    }

    @Override
    @Transactional
    public Pet adopt(Integer userId, String petType, String petName) {
        Pet exist = getByUserId(userId);
        if (exist != null) throw new RuntimeException("您已拥有宠物，不可重复领养");

        Pet pet = new Pet();
        pet.setUserId(userId);
        pet.setPetType(petType);
        pet.setPetName(petName == null || petName.trim().isEmpty() ? "小可爱" : petName);
        pet.setColor("#FFB6C1");
        pet.setHunger(70);
        pet.setCoins(0);
        pet.setAvatarUrl("/images/pet/" + petType + "_default.png");
        pet.setMood("happy");
        pet.setConsecutiveDays(0);
        save(pet);
        return pet;
    }

    @Override
    @Transactional
    public boolean feed(Integer userId, int feedAmount) {
        Pet pet = getByUserId(userId);
        if (pet == null) return false;
        int cost = 10;
        if (pet.getCoins() < cost) throw new RuntimeException("萌宠币不足");
        pet.setCoins(pet.getCoins() - cost);
        int newHunger = Math.min(100, pet.getHunger() + feedAmount);
        pet.setHunger(newHunger);
        updateMood(pet);       // 更新心情
        updateById(pet);
        return true;
    }

    @Override
    @Transactional
    public boolean play(Integer userId, int costCoins) {
        Pet pet = getByUserId(userId);
        if (pet == null) return false;
        if (pet.getCoins() < costCoins) {
            throw new RuntimeException("萌宠币不足");
        }
        pet.setCoins(pet.getCoins() - costCoins);
        return updateById(pet);
    }

    @Override
    @Transactional
    public boolean dye(Integer userId, String color) {
        Pet pet = getByUserId(userId);
        if (pet == null) return false;
        int cost = 30;
        if (pet.getCoins() < cost) throw new RuntimeException("萌宠币不足");
        pet.setCoins(pet.getCoins() - cost);
        pet.setColor(color);
        updateById(pet);
        return true;
    }

    @Override
    @Transactional
    public boolean rename(Integer userId, String newName) {
        Pet pet = getByUserId(userId);
        if (pet == null) return false;
        pet.setPetName(newName);
        updateById(pet);
        return true;
    }

    @Override
    @Transactional
    public boolean addCoins(Integer userId, int coins) {
        Pet pet = getByUserId(userId);
        if (pet == null) return false;
        pet.setCoins(pet.getCoins() + coins);
        updateById(pet);
        return true;
    }

    @Override
    @Transactional
    public boolean checkDailyLoginReward(Integer userId) {
        Pet pet = getByUserId(userId);
        if (pet == null) return false;

        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayStr = sdf.format(today);

        if (pet.getLastLoginDate() == null || !sdf.format(pet.getLastLoginDate()).equals(todayStr)) {
            pet.setCoins(pet.getCoins() + 30);
            pet.setLastLoginDate(today);
            updateById(pet);
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public int decreaseHungerForAll() {
        return petMapper.decreaseHungerForAll();
    }

    // ================== 心情更新（私有辅助） ==================
    private void updateMood(Pet pet) {
        int hunger = pet.getHunger();
        Integer consecutive = pet.getConsecutiveDays();
        int days = consecutive == null ? 0 : consecutive;
        if (hunger <= 20) {
            pet.setMood("sad");
        } else if (hunger >= 80) {
            pet.setMood("happy");
        } else if (days >= 3) {
            pet.setMood("excited");
        } else {
            pet.setMood("calm");
        }
    }

    // ================== 商店系统（需要 PetItem 表） ==================
    @Override
    @Transactional
    public boolean buyFood(Integer userId, String itemType, int quantity) {
        // 定义价格
        int price = getPriceByItemType(itemType);
        Pet pet = getByUserId(userId);
        if (pet == null || pet.getCoins() < price * quantity) return false;

        pet.setCoins(pet.getCoins() - price * quantity);
        updateById(pet);

        // 增加道具数量
        PetItem item = petItemMapper.selectOne(new LambdaQueryWrapper<PetItem>()
                .eq(PetItem::getUserId, userId)
                .eq(PetItem::getItemType, itemType));
        if (item == null) {
            item = new PetItem();
            item.setUserId(userId);
            item.setItemType(itemType);
            item.setQuantity(quantity);
            petItemMapper.insert(item);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
            petItemMapper.updateById(item);
        }
        return true;
    }

    @Override
    @Transactional
    public boolean useFood(Integer userId, String itemType) {
        PetItem item = petItemMapper.selectOne(new LambdaQueryWrapper<PetItem>()
                .eq(PetItem::getUserId, userId)
                .eq(PetItem::getItemType, itemType));
        if (item == null || item.getQuantity() <= 0) return false;

        int hungerInc = getHungerInc(itemType);
        Pet pet = getByUserId(userId);
        if (pet == null) return false;

        int newHunger = Math.min(100, pet.getHunger() + hungerInc);
        pet.setHunger(newHunger);
        updateMood(pet);
        updateById(pet);

        item.setQuantity(item.getQuantity() - 1);
        if (item.getQuantity() == 0) {
            petItemMapper.deleteById(item.getId());
        } else {
            petItemMapper.updateById(item);
        }
        return true;
    }

    @Override
    public Map<String, Integer> getUserItems(Integer userId) {
        Map<String, Integer> result = new HashMap<>();
        LambdaQueryWrapper<PetItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PetItem::getUserId, userId);
        for (PetItem item : petItemMapper.selectList(wrapper)) {
            result.put(item.getItemType(), item.getQuantity());
        }
        return result;
    }

    private int getPriceByItemType(String itemType) {
        switch (itemType) {
            case "food_small": return 10;
            case "food_medium": return 20;
            case "food_large": return 30;
            default: return 0;
        }
    }

    private int getHungerInc(String itemType) {
        switch (itemType) {
            case "food_small": return 10;
            case "food_medium": return 20;
            case "food_large": return 30;
            default: return 0;
        }
    }
}