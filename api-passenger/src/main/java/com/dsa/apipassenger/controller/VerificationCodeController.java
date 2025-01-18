package com.dsa.apipassenger.controller;

import com.dsa.apipassenger.request.VerificationCodeDTO;
import com.dsa.apipassenger.service.VerificationCodeService;
import com.dsa.internalcommon.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VerificationCodeController {

    @Autowired
    VerificationCodeService verificationCodeService;

    @GetMapping("/verification-code")
    public ResponseResult verificationCode(@RequestBody VerificationCodeDTO verificationCodeDTO){

        String passengerPhone = verificationCodeDTO.getPassengerPhone();



        return verificationCodeService.generatorCode(passengerPhone);
    }
}
