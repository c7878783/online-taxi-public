package com.dsa.apipassenger.service;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.PassengerUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

@Service
@Slf4j
public class UserService {

    public ResponseResult getUserByAccessToken(String accessToken){
        log.info(accessToken);
        //解析 拿到手机号
        PassengerUser passengerUser = new PassengerUser();
        passengerUser.setPassengerName("sna");
        passengerUser.setProfilePhoto("头像");

        //根据手机号返回信息
        return ResponseResult.success(passengerUser);
    }
}
