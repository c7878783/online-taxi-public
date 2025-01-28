package com.dsa.apiboss.controller;

import com.dsa.apiboss.remote.ServiceDriverUserClient;
import com.dsa.apiboss.service.CarService;
import com.dsa.apiboss.service.DriverUserService;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Car;
import com.dsa.internalcommon.pojo.DriverCarBindingRelationship;
import com.dsa.internalcommon.pojo.DriverUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DriverUserController {

    @Autowired
    DriverUserService driverUserService;
    @Autowired
    CarService carService;

    @PostMapping("/driver-user")
    public ResponseResult addDriverUser(@RequestBody DriverUser driverUser){

        return driverUserService.addDriverUser(driverUser);
    }

    @PutMapping("/driver-user")
    public ResponseResult updateDriverUser(@RequestBody DriverUser driverUser){

        return driverUserService.updateDriverUser(driverUser);
    }

    @PostMapping("/diverCarRelationship/bind")
    public ResponseResult bind(@RequestBody DriverCarBindingRelationship driverCarBindingRelationship){

        return carService.bind(driverCarBindingRelationship);
    }

    @PostMapping("/diverCarRelationship/unbind")
    public ResponseResult unbind(@RequestBody DriverCarBindingRelationship driverCarBindingRelationship){

        return carService.unbind(driverCarBindingRelationship);
    }

    @PostMapping("/car")
    public ResponseResult addCar(@RequestBody Car car){

        return carService.addCar(car);
    }

}
