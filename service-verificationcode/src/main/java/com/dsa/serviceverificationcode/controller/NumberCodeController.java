package com.dsa.serviceverificationcode.controller;

import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.NumberCodeResponse;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class NumberCodeController {

    @GetMapping("/numberCode/{size}")
    public ResponseResult getNumberCode(@PathVariable("size") Integer size){

        System.out.println(size);

        double randomNum = (Math.random() * 9 + 1) * Math.pow(10, size - 1);
        int resultInt = (int)randomNum;
//        System.out.println(resultInt);

        NumberCodeResponse response = new NumberCodeResponse();
        response.setNumberCode(resultInt);


        return ResponseResult.success(response);
    }

}
