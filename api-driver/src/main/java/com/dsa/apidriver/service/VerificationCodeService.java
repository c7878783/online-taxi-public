package com.dsa.apidriver.service;

import com.dsa.internalcommon.dto.ResponseResult;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeService {

    public ResponseResult checkAndSendVerificationCode(String driverPhone){
        //查询service-driver-user，该说记号是否存在

        //获取验证码

        //调用第三方验证码

        //存入redis


        return ResponseResult.success("");
    }
}
