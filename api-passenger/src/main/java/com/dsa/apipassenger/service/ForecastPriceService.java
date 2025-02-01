package com.dsa.apipassenger.service;

import com.dsa.apipassenger.remote.ServicePriceClient;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.ForecastPriceDTO;
import com.dsa.internalcommon.responese.ForecastPriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ForecastPriceService {

    @Autowired
    ServicePriceClient servicePriceClient;

    /**
     * 根据出发地和目的地经纬度，计算预估价格
     * @param depLongitude
     * @param depLatitude
     * @param destLongitude
     * @param destLatitude
     * @return
     */
    public ResponseResult forecastPrice(String depLongitude, String depLatitude, String destLongitude, String destLatitude,
                                        String cityCode, String vehicleType
    ){

        log.info("出发地经度：" + depLongitude);
        log.info("出发地纬度：" + depLatitude);
        log.info("目的地经度：" + destLongitude);
        log.info("目的地纬度：" + destLatitude);


        log.info("调用计价服务，计算价格");
        ForecastPriceDTO forecastPriceDTO = new ForecastPriceDTO();
        forecastPriceDTO.setDepLongitude(depLongitude);
        forecastPriceDTO.setDepLatitude(depLatitude);
        forecastPriceDTO.setDestLongitude(destLongitude);
        forecastPriceDTO.setDestLatitude(destLatitude);
        forecastPriceDTO.setCityCode(cityCode);
        forecastPriceDTO.setVehicleType(vehicleType);
        ResponseResult<ForecastPriceResponse> forecast = servicePriceClient.forecastPrice(forecastPriceDTO);
//        ForecastPriceResponse forecastData = forecast.getData();
//        double price = forecastData.getPrice();
//        String cityCode = forecastData.getCityCode();
//        String vehicleType = forecastData.getVehicleType();
//        String fareType = forecastData.getFareType();
//        Integer fareVersion = forecastData.getFareVersion();
//        ForecastPriceResponse forecastPriceResponse = new ForecastPriceResponse();
//        forecastPriceResponse.setPrice(price);
//        forecastPriceResponse.setCityCode(cityCode);
//        forecastPriceResponse.setVehicleType(vehicleType);
//        forecastPriceResponse.setFareType(fareType);
//        forecastPriceResponse.setFareVersion(fareVersion);

        return ResponseResult.success(forecast.getData());
    }

}
