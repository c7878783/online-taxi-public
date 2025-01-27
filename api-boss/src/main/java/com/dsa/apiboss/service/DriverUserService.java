package com.dsa.apiboss.service;

import com.dsa.apiboss.remote.ServiceDriverUserClient;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.DriverUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class DriverUserService {


    @Autowired
    ServiceDriverUserClient serviceDriverUserClient;

    public ResponseResult addDriverUser(DriverUser driverUser){

        return serviceDriverUserClient.addDriverUser(driverUser);
    }
}
