package com.dsa.apipassenger.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeService {
    public String generatorCode(String passengerPhone){
        System.out.println("请用验证码，获取验证码");
        String code = "1111";

        System.out.println("存入redis");

        JSONObject result = new JSONObject();
        result.put("code", 1);
        result.put("message","成功");
        return result.toString();
    }
}
