package com.dsa.servicemap.controller;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.ForecastPriceDTO;
import com.dsa.internalcommon.responese.DirectionResponse;
import com.dsa.servicemap.service.DirectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class DirectionController {

    @Autowired
    private DirectionService directionService;

    @GetMapping("/direction/driving")
    public ResponseResult<DirectionResponse> direction(@RequestParam("depLongitude") String depLongitude,
                                                       @RequestParam("depLatitude") String depLatitude,
                                                       @RequestParam("destLongitude") String destLongitude,
                                                       @RequestParam("destLatitude") String destLatitude){

//        String depLongitude = forecastPriceDTO.getDepLongitude();
//        String depLatitude = forecastPriceDTO.getDepLatitude();
//        String destLongitude = forecastPriceDTO.getDestLongitude();
//        String destLatitude = forecastPriceDTO.getDepLatitude();

        return directionService.direction(depLongitude, depLatitude, destLongitude, destLatitude);
    }
}
