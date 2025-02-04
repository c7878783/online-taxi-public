package com.dsa.serviceprice.controller;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.ForecastPriceDTO;
import com.dsa.serviceprice.service.ForecastPriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ForecastPriceController {

    @Autowired
    private ForecastPriceService forecastPriceService;

    /**
     * 发起订单前预估价格
     * @param forecastPriceDTO
     * @return
     */
    @PostMapping("/forecast-price")
    public ResponseResult forecastPrice(@RequestBody ForecastPriceDTO forecastPriceDTO){

        String depLongitude = forecastPriceDTO.getDepLongitude();
        String depLatitude = forecastPriceDTO.getDepLatitude();
        String destLongitude = forecastPriceDTO.getDestLongitude();
        String destLatitude = forecastPriceDTO.getDepLatitude();
        String cityCode = forecastPriceDTO.getCityCode();
        String vehicleType = forecastPriceDTO.getVehicleType();

        return forecastPriceService.forecastPrice(depLongitude, depLatitude, destLongitude, destLatitude, cityCode, vehicleType);
    }

    /**
     * 行程结束后计算实际价格
     * @param distance
     * @param duration
     * @param fareType
     * @return
     */
    @PostMapping("/calculate-price")
    public ResponseResult calculatePrice(@RequestParam Integer distance, @RequestParam Integer duration, @RequestParam String fareType){
        return forecastPriceService.calculatePrice(distance, duration, fareType);
    }
}
