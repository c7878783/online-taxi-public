package com.dsa.apidriver.controller;

import com.dsa.apidriver.service.VerificationCodeService;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.VerificationCodeDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
//@CrossOrigin(origins = "http://localhost:3001")
@RestController
@Slf4j
public class VerificationCodeController {

    @Autowired
    VerificationCodeService verificationCodeService;

    @PostMapping("/verification-code")
    public ResponseResult verificationCode(@RequestBody VerificationCodeDTO verificationCodeDTO){
        String driverPhone = verificationCodeDTO.getDriverPhone();

        log.info("准备进入verificationCodeService：checkAndSendVerificationCode"+" 手机号是：" +driverPhone);
        return verificationCodeService.checkAndSendVerificationCode(driverPhone);
    }


    /**
     * 校验验证码
     * @param verificationCodeDTO 用来接受手机号和验证码的Body体变量
     * @return 调用service检查手机号和验证码并查询(注册)用户，发布token
     */
    @PostMapping("/verification-code-check")
    public ResponseResult checkCode(@RequestBody VerificationCodeDTO verificationCodeDTO){

        String driverPhone = verificationCodeDTO.getDriverPhone();
        String verificationCode = verificationCodeDTO.getVerificationCode();
        System.out.println(driverPhone + ":" + verificationCode);
        return verificationCodeService.checkCode(driverPhone, verificationCode);
    }
}
