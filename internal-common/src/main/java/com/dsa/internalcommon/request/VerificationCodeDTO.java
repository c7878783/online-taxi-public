package com.dsa.internalcommon.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VerificationCodeDTO {

    private String passengerPhone;
    private String verificationCode;
    private String driverPhone;

}
