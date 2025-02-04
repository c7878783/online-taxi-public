package com.dsa.apidriver.service;

import com.dsa.internalcommon.dto.ResponseResult;
import org.springframework.web.bind.annotation.RequestParam;

public class PayController {

    public ResponseResult pushPayInfo(String orderId, String price, Long passengerId){
        //封装消息

        //推送消息


        return ResponseResult.success();

    }
}
