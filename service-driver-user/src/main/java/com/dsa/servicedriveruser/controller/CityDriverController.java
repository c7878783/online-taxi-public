package com.dsa.servicedriveruser.controller;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.servicedriveruser.service.CityDriverUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/city-driver")
public class CityDriverController {

    @Autowired
    CityDriverUserService cityDriverUserService;
    @GetMapping("/is-available-driver")
    public ResponseResult<Boolean> isAvailableDriver(@RequestParam String cityCode){
        return cityDriverUserService.isAvailableDriver(cityCode);
    }
}
