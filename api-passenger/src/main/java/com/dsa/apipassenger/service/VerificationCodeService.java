package com.dsa.apipassenger.service;

import com.dsa.apipassenger.remote.ServiceVerificationCodeClient;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.NumberCodeResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeService {

    @Autowired
    ServiceVerificationCodeClient serviceVerificationCodeClient;

    public String generatorCode(String passengerPhone){


        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerificationCodeClient.getNumberCode(5);
        int NumberCode = numberCodeResponse.getData().getNumberCode();

        System.out.println("获取验证码:" + NumberCode);
//        System.out.println("获取验证码");
//        System.out.println("存入redis");

        JSONObject result = new JSONObject();
        result.put("code", 1);
        result.put("message","成功");
        return result.toString();
    }
}
