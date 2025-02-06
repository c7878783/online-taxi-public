package com.dsa.apidriver.service;

import com.dsa.apidriver.remote.ServiceDriverUserClient;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.DriverUser;
import com.dsa.internalcommon.pojo.DriverUserWorkStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    ServiceDriverUserClient serviceDriverUserClient;

    public ResponseResult updateDriverUser(DriverUser driverUser) {

        return serviceDriverUserClient.updateDriverUser(driverUser);
    }

    public ResponseResult changeWorkStatus(DriverUserWorkStatus driverUserWorkStatus) {
        return serviceDriverUserClient.changeWorkStatus(driverUserWorkStatus);
    }

    public  ResponseResult getDriverCarBindingRelationship(String driverPhone) {
        return serviceDriverUserClient.getDriverCarBindingRelationship(driverPhone);
    }
}
