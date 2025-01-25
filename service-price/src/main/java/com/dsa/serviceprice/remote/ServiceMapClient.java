package com.dsa.serviceprice.remote;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.ForecastPriceDTO;
import com.dsa.internalcommon.responese.DirectionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("service-map")
public interface ServiceMapClient {

    @GetMapping("/direction/driving/{depLongitude}&{depLatitude}&{destLongitude}&{destLatitude}")
    public ResponseResult<DirectionResponse> direction(@PathVariable("depLongitude") String depLongitude,
                                                       @PathVariable("depLatitude") String depLatitude,
                                                       @PathVariable("destLongitude") String destLongitude,
                                                       @PathVariable("destLatitude") String destLatitude);
}
