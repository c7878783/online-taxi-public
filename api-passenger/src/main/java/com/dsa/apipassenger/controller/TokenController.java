package com.dsa.apipassenger.controller;

import com.dsa.apipassenger.service.TokenService;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.TokenResponse;
import org.apache.el.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TokenController {

    @Autowired
    private TokenService tokenService;

    /**
     * 根据请求Body中的refreshToken获取新的token组合
     * @param tokenResponse Body中的请求变量json，应该含有refreshToken
     * @return 调用service获取新的token组合
     */
    @PostMapping("/token-refresh")
    public ResponseResult refreshToken(@RequestBody TokenResponse tokenResponse){

        String refreshToken = tokenResponse.getRefreshToken();

        return tokenService.refreshToken(refreshToken);
    }
}
