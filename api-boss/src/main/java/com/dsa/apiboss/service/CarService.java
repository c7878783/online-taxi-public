package com.dsa.apiboss.service;

import com.dsa.apiboss.remote.ServiceDriverUserClient;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarService {

    @Autowired
    ServiceDriverUserClient serviceDriverUserClient;

    public ResponseResult addCar(Car car){

        return serviceDriverUserClient.addCar(car);
    }
}
