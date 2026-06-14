package com.lan.accountbook.sys.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/pet/petPage")
    public String petPage() {
        return "pet";
    }

}