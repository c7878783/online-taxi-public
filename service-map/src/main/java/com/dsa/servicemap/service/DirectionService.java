package com.dsa.servicemap.service;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.DirectionResponse;
import com.dsa.servicemap.remote.MapDirectionClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DirectionService {

    @Autowired
    private MapDirectionClient mapDirectionClient;

    public ResponseResult<DirectionResponse> direction(String depLongitude, String depLatitude, String destLongitude, String destLatitude){
        DirectionResponse direction = mapDirectionClient.direction(depLongitude, depLatitude, destLongitude, destLatitude);

        return ResponseResult.success(direction);
    }
}
