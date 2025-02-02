package com.dsa.apidriver.controller;

import com.dsa.apidriver.service.UserService;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.DriverUser;
import com.dsa.internalcommon.pojo.DriverUserWorkStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    UserService userService;


    @PutMapping("/user")
    public ResponseResult updateDriverUser(@RequestBody DriverUser driverUser){
        return userService.updateDriverUser(driverUser);
    }

    @PutMapping("/driver-user-work-status")
    public ResponseResult changeWorkStatus(@RequestBody DriverUserWorkStatus driverUserWorkStatus){


        return userService.changeWorkStatus(driverUserWorkStatus);
    }
}
