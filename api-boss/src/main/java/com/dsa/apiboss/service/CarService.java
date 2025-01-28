package com.dsa.apiboss.service;

import com.dsa.apiboss.remote.ServiceDriverUserClient;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Car;
import com.dsa.internalcommon.pojo.DriverCarBindingRelationship;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CarService {

    @Autowired
    ServiceDriverUserClient serviceDriverUserClient;

    public ResponseResult addCar(Car car){

        return serviceDriverUserClient.addCar(car);
    }

    public ResponseResult bind(DriverCarBindingRelationship driverCarBindingRelationship) {

        return serviceDriverUserClient.bind(driverCarBindingRelationship);
    }

    public ResponseResult unbind(DriverCarBindingRelationship driverCarBindingRelationship) {

        return serviceDriverUserClient.unbind(driverCarBindingRelationship);
    }
}
