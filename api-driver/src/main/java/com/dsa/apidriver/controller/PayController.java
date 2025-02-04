package com.dsa.apidriver.controller;

import com.dsa.internalcommon.dto.ResponseResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pay")
public class PayController {


    @GetMapping("/push-pay-info")
    public ResponseResult pushPayInfo(@RequestParam String orderId, @RequestParam String price, @RequestParam Long passengerId){

    }
}
