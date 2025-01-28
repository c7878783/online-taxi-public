package com.dsa.servicedriveruser.service;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Car;
import com.dsa.servicedriveruser.mapper.CarMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CarService {

    @Autowired
    private CarMapper carMapper;

    public ResponseResult addCar(Car car){
        LocalDateTime now = LocalDateTime.now();

        car.setGmtCreate(now);
        car.setGmtModified(now);
        carMapper.insert(car);

        return ResponseResult.success();
    }

}
