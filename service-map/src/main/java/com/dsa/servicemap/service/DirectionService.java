package com.dsa.servicemap.service;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.DirectionResponse;
import org.springframework.stereotype.Service;

@Service
public class DirectionService {

    public ResponseResult driving(String depLongitude, String depLatitude, String destLongitude, String destLatitude){

        DirectionResponse directionResponse = new DirectionResponse();
        directionResponse.setDistance(10);
        directionResponse.setDuration(2);
        return ResponseResult.success(directionResponse);
    }
}
