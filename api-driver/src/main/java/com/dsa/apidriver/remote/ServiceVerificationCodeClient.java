package com.dsa.apidriver.remote;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.NumberCodeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient("service-verificationcode")
public interface ServiceVerificationCodeClient {

    @RequestMapping(method = RequestMethod.GET, value = "/numberCode/{size}")
    public ResponseResult<NumberCodeResponse> getNumberCode(@PathVariable("size") int size);
}

