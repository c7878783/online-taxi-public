package com.dsa.apidriver.service;

import com.dsa.apidriver.remote.ServiceDriverUserClient;
import com.dsa.apidriver.remote.ServiceVerificationCodeClient;
import com.dsa.internalcommon.constant.CommonStatusEnum;
import com.dsa.internalcommon.constant.DriverCarConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.DriverUserExistsResponse;
import com.dsa.internalcommon.responese.NumberCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class VerificationCodeService {

    @Autowired
    ServiceDriverUserClient serviceDriverUserClient;
    @Autowired
    ServiceVerificationCodeClient serviceVerificationCodeClient;

    public ResponseResult checkAndSendVerificationCode(String driverPhone){
        //查询service-driver-user，该说记号是否存在
        ResponseResult<DriverUserExistsResponse> driverUserExistsResponseResponseResult = serviceDriverUserClient.getDriverUser(driverPhone);
        DriverUserExistsResponse data = driverUserExistsResponseResponseResult.getData();
        int ifExists = data.getIfexists();
        if (ifExists == DriverCarConstants.DRIVER_NOT_EXISTS){
            return ResponseResult.fail(CommonStatusEnum.DRIVER_NOT_EXISTS.getCode(), CommonStatusEnum.DRIVER_NOT_EXISTS.getValue());
        }
        log.info(driverPhone + "司机存在");
        //获取验证码
        ResponseResult<NumberCodeResponse> numberCodeResult = serviceVerificationCodeClient.getNumberCode(5);
        NumberCodeResponse numberCodeResponse = numberCodeResult.getData();
        int numberCode = numberCodeResponse.getNumberCode();
        log.info("验证码是"+numberCode);

        //调用第三方验证码

        //存入redis


        return ResponseResult.success("");
    }
}
