package com.dsa.servicepassengeruser.service;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.VerificationCodeDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class UserService {


    public static ResponseResult loginOrRegister(String passengerPhone) {
        System.out.println("进入了loginOrRegister方法");
        System.out.println("手机号是" + passengerPhone);
        return ResponseResult.success();
    }
}
