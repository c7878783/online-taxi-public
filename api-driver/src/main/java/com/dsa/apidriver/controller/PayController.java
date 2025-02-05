package com.dsa.apidriver.controller;

import com.dsa.apidriver.service.PayService;
import com.dsa.internalcommon.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Autowired
    PayService payService;

    /**
     * 司机发起收款请求
     * @param orderId
     * @param price
     * @param passengerId
     * @return
     */
    @PostMapping("/push-pay-info")
    public ResponseResult pushPayInfo(@RequestParam Long orderId, @RequestParam String price, @RequestParam Long passengerId){
        return payService.pushPayInfo(orderId, price, passengerId);
    }
}
