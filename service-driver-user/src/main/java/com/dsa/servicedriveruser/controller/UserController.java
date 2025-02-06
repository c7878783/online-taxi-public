package com.dsa.servicedriveruser.controller;

import com.dsa.internalcommon.constant.DriverCarConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.dto.TokenResult;
import com.dsa.internalcommon.pojo.DriverCarBindingRelationship;
import com.dsa.internalcommon.pojo.DriverUser;
import com.dsa.internalcommon.responese.DriverUserExistsResponse;
import com.dsa.internalcommon.responese.OrderDriverResponse;
import com.dsa.internalcommon.util.JwtUtils;
import com.dsa.servicedriveruser.service.DriverUserService;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private DriverUserService driverUserService;

    @PostMapping("/user")
    private ResponseResult addDriverUser(@RequestBody DriverUser driverUser){
        return driverUserService.addDriverUser(driverUser);
    }

    @PutMapping("/user")
    public ResponseResult updateDriverUser(@RequestBody DriverUser driverUser){

        return driverUserService.updateDriverUser(driverUser);
    }

    @GetMapping("/check-driver/{driverPhone}")
    private ResponseResult<DriverUserExistsResponse> getDriverUser(@PathVariable String driverPhone){
//        String driverPhone = driverUser.getDriverPhone();
        ResponseResult<DriverUser> driverUserByPhone = driverUserService.getDriverUserByPhone(driverPhone);
        DriverUser driverUserDb = driverUserByPhone.getData();
        int ifExists = DriverCarConstants.DRIVER_EXISTS;
        DriverUserExistsResponse response = new DriverUserExistsResponse();

        if(driverUserDb == null){
            ifExists = DriverCarConstants.DRIVER_NOT_EXISTS;
//            response.setDriverPhone(driverUserDb.getDriverPhone());
            response.setIfexists(ifExists);
        }else {
            response.setDriverPhone(driverUserDb.getDriverPhone());
            response.setIfexists(ifExists);
        }


        return ResponseResult.success(response);
    }

    @GetMapping("/get-available-driver/{carId}")
    public ResponseResult<OrderDriverResponse> getAvailableDriver(@PathVariable("carId") Long carId){
        return driverUserService.getAvailableDriver(carId);
    }

    @GetMapping("/driver-car-binding-relationship")
    public ResponseResult<DriverCarBindingRelationship> getDriverCarBindingRelationship(@RequestParam String driverPhone){

        return driverUserService.getDriverCarBindingRelationship(driverPhone);

    }

}
