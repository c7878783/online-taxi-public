package com.dsa.testalipay.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @PostMapping("/test")
    public String test(){
        System.out.println("支付宝");
        return "穿透测试";
    }

}
