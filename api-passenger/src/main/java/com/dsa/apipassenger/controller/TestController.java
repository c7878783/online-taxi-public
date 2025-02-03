package com.dsa.apipassenger.controller;

import com.dsa.apipassenger.remote.ServiceOrderClient;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试用的类
 */
@RestController
public class TestController {

    @Autowired
    ServiceOrderClient serviceOrderClient;

    @GetMapping("/testController")
    public String test(){
        return "test";
    }

    @GetMapping("/authTest")
    public ResponseResult authTest(){
        return ResponseResult.success("auth test");
    }

    @GetMapping("/noauthTest")
    public ResponseResult noauthTest(){
        return ResponseResult.success("noauth test");
    }

    @GetMapping("/test-real-time-order")
    public String dispatchRealaTimeOrder(@RequestParam long orderId){

        return serviceOrderClient.dispatchRealaTimeOrder(orderId);
    }
}
