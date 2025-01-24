package com.dsa.apipassenger.controller;

import com.dsa.apipassenger.service.UserService;
import com.dsa.internalcommon.dto.ResponseResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/users")
    public ResponseResult getUser(HttpServletRequest request){
        //从http请求中获取accesstoken
        String accessToken = request.getHeader("Authorization");

        return userService.getUserByAccessToken(accessToken);
    }
}
