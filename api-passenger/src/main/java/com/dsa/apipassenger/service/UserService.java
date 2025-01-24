package com.dsa.apipassenger.service;

import com.dsa.apipassenger.remote.ServicePassengerUserClient;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.dto.TokenResult;
import com.dsa.internalcommon.pojo.PassengerUser;
import com.dsa.internalcommon.request.VerificationCodeDTO;
import com.dsa.internalcommon.util.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

@Service
@Slf4j
public class UserService {

    @Autowired
    ServicePassengerUserClient servicePassengerUserClient;

    public ResponseResult getUserByAccessToken(String accessToken){
        log.info(accessToken);
        //解析 拿到手机号
        TokenResult tokenResult = JwtUtils.checkToken(accessToken);
        String phone = tokenResult.getPhone();
        log.info("解析手机号:"+phone);

//        VerificationCodeDTO verificationCodeDTO = new VerificationCodeDTO();
//        verificationCodeDTO.setPassengerPhone(phone);
        ResponseResult userByPhone = servicePassengerUserClient.getUser(phone);


        //根据手机号返回信息
        return ResponseResult.success(userByPhone.getData());
    }
}
