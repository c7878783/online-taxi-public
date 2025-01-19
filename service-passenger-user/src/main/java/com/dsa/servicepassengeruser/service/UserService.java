package com.dsa.servicepassengeruser.service;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.servicepassengeruser.pojo.PassengerUser;
import com.dsa.servicepassengeruser.mapper.PassengerUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    @Autowired
    PassengerUserMapper passengerUserMapper;

    public ResponseResult loginOrRegister(String passengerPhone) {

        Map<String, Object> map = new HashMap<>();
        map.put("passenger_phone", passengerPhone);
        List<PassengerUser> passengerUsers = passengerUserMapper.selectByMap(map);
        System.out.println(passengerUsers);

        if (passengerUsers.size() == 0) {
            PassengerUser passengerUser = new PassengerUser();
            passengerUser.setPassengerName("用户" + passengerPhone);
            passengerUser.setPassengerGender((byte) 1);//这种应该设置constant
            passengerUser.setPassengerPhone(passengerPhone);
            passengerUser.setState((byte) 1);
            passengerUser.setGmtCreate(LocalDateTime.now());
            passengerUser.setGmtModified(LocalDateTime.now());
            passengerUserMapper.insert(passengerUser);
//            return ResponseResult.success(passengerUser);


        }
        return ResponseResult.success();
    }
}

