package com.dsa.apipassenger.controller;


import com.dsa.apipassenger.service.VerificationCodeService;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.VerificationCodeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationCodeController {

    @Autowired
    VerificationCodeService verificationCodeService;

    /**
     * 验证码生成
     * @param verificationCodeDTO 用来接受手机号passengerPhone的Body体变量
     * @return 调用service生成验证码
     */
    @GetMapping("/verification-code")
    public ResponseResult verificationCode(@RequestBody VerificationCodeDTO verificationCodeDTO){

        String passengerPhone = verificationCodeDTO.getPassengerPhone();
        return verificationCodeService.generatorCode(passengerPhone);
    }

    /**
     * 校验验证码
     * @param verificationCodeDTO 用来接受手机号和验证码的Body体变量
     * @return 调用service检查手机号和验证码并查询(注册)用户，发布token
     */
    @PostMapping("/verification-code-check")
    public ResponseResult checkCode(@RequestBody VerificationCodeDTO verificationCodeDTO){

        String passengerPhone = verificationCodeDTO.getPassengerPhone();
        String verificationCode = verificationCodeDTO.getVerificationCode();
        System.out.println(passengerPhone + ":" + verificationCode);
        return verificationCodeService.checkCode(passengerPhone, verificationCode);
    }


}
