package com.lan.accountbook.sys.task;

import com.lan.accountbook.sys.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PetScheduledTasks {

    @Autowired
    private PetService petService;

    // 每小时整点执行：减少所有宠物的饥饿值并更新心情
    @Scheduled(cron = "0 0 * * * ?")
    public void decreaseHunger() {
        int updated = petService.decreaseHungerForAll();
        System.out.println("定时减少饥饿值，影响 " + updated + " 只宠物");
    }
}