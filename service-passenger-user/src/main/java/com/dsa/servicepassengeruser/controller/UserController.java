package com.dsa.servicepassengeruser.controller;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.PassengerUser;
import com.dsa.internalcommon.request.VerificationCodeDTO;
import com.dsa.servicepassengeruser.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.POST, value = "/user")
    public ResponseResult loginOrRegister(@RequestBody VerificationCodeDTO verificationCodeDTO) {
        String passengerPhone = verificationCodeDTO.getPassengerPhone();
        System.out.println("手机号是" + passengerPhone);
        return userService.loginOrRegister(passengerPhone);
    }

    /**
     * POST /user 和 GET /user 是不同的 HTTP 方法，Spring 允许不同方法使用相同路径
     * 参数用body去传，会有bug报错，改成显式的传参数,这是一个get和post有关的bug：feign调用post转get
     */
    @GetMapping("/user/{phone}")
    public ResponseResult<PassengerUser> getUser(@PathVariable("phone") String passengerPhone){
//        String passengerPhone = verificationCodeDTO.getPassengerPhone();
        return userService.getUserByPhone(passengerPhone);
    }
}
