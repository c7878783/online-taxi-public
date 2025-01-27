package com.dsa.serviceprice.remote;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.ForecastPriceDTO;
import com.dsa.internalcommon.responese.DirectionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("service-map")
public interface ServiceMapClient {

    @GetMapping("/direction/driving")
    public ResponseResult<DirectionResponse> direction(@RequestParam("depLongitude") String depLongitude,
                                                       @RequestParam("depLatitude") String depLatitude,
                                                       @RequestParam("destLongitude") String destLongitude,
                                                       @RequestParam("destLatitude") String destLatitude);
}
