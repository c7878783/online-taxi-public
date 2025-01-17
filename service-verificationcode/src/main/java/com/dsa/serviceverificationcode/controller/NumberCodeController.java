package com.dsa.serviceverificationcode.controller;

import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NumberCodeController {

    @GetMapping("/numberCode/{size}")
    public String getNumberCode(@PathVariable("size") Integer size){

        System.out.println(size);


        JSONObject result = new JSONObject();
        result.put("code", 1);
        result.put("message", "成功");
        JSONObject data = new JSONObject();
        data.put("numberCode", "123456");
        result.put("data", data);
        return result.toString();
    }
}
