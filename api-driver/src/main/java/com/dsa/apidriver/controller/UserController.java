package com.dsa.apidriver.controller;

import com.dsa.apidriver.service.UserService;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.dto.TokenResult;
import com.dsa.internalcommon.pojo.DriverUser;
import com.dsa.internalcommon.pojo.DriverUserWorkStatus;
import com.dsa.internalcommon.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/driver-car-binding-relationship")
    public ResponseResult getDriverCarBindingRelationship(HttpServletRequest httpServletRequest){

        String authorization = httpServletRequest.getHeader("Authorization");

        TokenResult tokenResult = JwtUtils.checkToken(authorization);
        String driverPhone = tokenResult.getPhone();

        return userService.getDriverCarBindingRelationship(driverPhone);

    }
}
