package com.dsa.apipassenger.controller;

import com.dsa.apipassenger.request.VerificationCodeDTO;
import com.dsa.apipassenger.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationCodeController {

    @Autowired
    VerificationCodeService verificationCodeService;

    @GetMapping("/verification-code")
    public String verificationCode(@RequestBody VerificationCodeDTO verificationCodeDTO){

        String passengerPhone = verificationCodeDTO.getPassengerPhone();
        System.out.println("收到的手机号" + passengerPhone);



        return verificationCodeService.generatorCode(passengerPhone);
    }
}
