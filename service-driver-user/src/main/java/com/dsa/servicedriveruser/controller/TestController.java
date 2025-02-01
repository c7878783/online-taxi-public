package com.dsa.servicedriveruser.controller;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.DriverUser;
import com.dsa.servicedriveruser.mapper.DriverUserMapper;
import com.dsa.servicedriveruser.service.DriverUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @Autowired
    DriverUserService driverUserService;
    @Autowired
    DriverUserMapper driverUserMapper;

    @GetMapping("/testController")
    public ResponseResult test(){
        return driverUserService.testGetDriverUser();
    }

    @GetMapping("/test-xml")
    public int testXml(String args){
        return driverUserMapper.select1(args);
    }
}
