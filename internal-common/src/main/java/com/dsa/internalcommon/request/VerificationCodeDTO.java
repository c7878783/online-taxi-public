package com.dsa.internalcommon.request;

import lombok.Data;

@Data
public class VerificationCodeDTO {

    private String passengerPhone;
    private String verificationCode;

}
