package com.dsa.apipassenger.controller;

import com.dsa.internalcommon.dto.ResponseResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试用的类
 */
@RestController
public class TestController {

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
}
