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

    @PostMapping("/token-refresh")
    public ResponseResult refreshToken(@RequestBody TokenResponse tokenResponse){

        String refreshToken = tokenResponse.getRefreshToken();

        return tokenService.refreshToken(refreshToken);
    }
}
