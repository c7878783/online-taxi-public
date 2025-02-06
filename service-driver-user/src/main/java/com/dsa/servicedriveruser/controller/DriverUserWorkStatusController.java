package com.dsa.servicedriveruser.controller;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.DriverUserWorkStatus;
import com.dsa.servicedriveruser.service.DriverUserWorkStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

@RestController
public class DriverUserWorkStatusController {

    @Autowired
    DriverUserWorkStatusService driverUserWorkStatusService;

    @PutMapping("/driver-user-work-status")
    public ResponseResult changeWorkStatus(@RequestBody DriverUserWorkStatus driverUserWorkStatus){

        Long driverId = driverUserWorkStatus.getDriverId();
        Integer workStatus = driverUserWorkStatus.getWorkStatus();

        return driverUserWorkStatusService.changeWorkStatus(driverId, workStatus);
    }

    @PostMapping("/driver-user-work-status")
    public ResponseResult<DriverUserWorkStatus> getWorkStatus(@RequestBody DriverUserWorkStatus driverUserWorkStatus){


        return driverUserWorkStatusService.getWorkStatus(driverUserWorkStatus);
    }

}
