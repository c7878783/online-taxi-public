package com.dsa.apidriver.remote;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Car;
import com.dsa.internalcommon.pojo.DriverCarBindingRelationship;
import com.dsa.internalcommon.pojo.DriverUser;
import com.dsa.internalcommon.pojo.DriverUserWorkStatus;
import com.dsa.internalcommon.responese.DriverUserExistsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("service-driver-user")
public interface ServiceDriverUserClient {

    @RequestMapping(method = RequestMethod.PUT, value = "/user")
    public ResponseResult updateDriverUser(@RequestBody DriverUser driverUser);

    @RequestMapping (method = RequestMethod.GET, value = "/check-driver/{driverPhone}")
    public ResponseResult<DriverUserExistsResponse> getDriverUser(@PathVariable String driverPhone);

    @RequestMapping(method = RequestMethod.GET, value = "/car")
    public ResponseResult<Car> getCarById(@RequestParam Long carId);

    @PutMapping("/driver-user-work-status")
    public ResponseResult changeWorkStatus(@RequestBody DriverUserWorkStatus driverUserWorkStatus);

    @GetMapping("/driver-car-binding-relationship")
    public ResponseResult<DriverCarBindingRelationship> getDriverCarBindingRelationship(@RequestParam String driverPhone);

    @PostMapping("/driver-user-work-status")
    public ResponseResult<DriverUserWorkStatus> getWorkStatus(@RequestBody DriverUserWorkStatus driverUserWorkStatus);
}
