package com.dsa.servicedriveruser.service;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.servicedriveruser.mapper.DriverUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CityDriverUserService {

    @Autowired
    DriverUserMapper driverUserMapper;

    public ResponseResult<Boolean> isAvailableDriver(String cityCode){
        if (driverUserMapper.selectDriverUserCountByCityCode(cityCode) > 0){
            return ResponseResult.success(true);
        }else {
            return ResponseResult.success(false);
        }
    }
}
