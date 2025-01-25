package com.dsa.serviceprice.service;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.ForecastPriceDTO;
import com.dsa.internalcommon.responese.DirectionResponse;
import com.dsa.internalcommon.responese.ForecastPriceResponse;
import com.dsa.serviceprice.remote.ServiceMapClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ForecastPriceService {

        @Autowired
        ServiceMapClient serviceMapClient;

        public ResponseResult forecastPrice(String depLongitude, String depLatitude, String destLongitude, String destLatitude){
            log.info("出发地经度：" + depLongitude);
            log.info("出发地纬度：" + depLatitude);
            log.info("目的地经度：" + destLongitude);
            log.info("目的地纬度：" + destLatitude);


            log.info("调用地图服务service-map，查询距离和时长");
//            ForecastPriceDTO forecastPriceDTO = new ForecastPriceDTO();
//            forecastPriceDTO.setDepLongitude(depLongitude);
//            forecastPriceDTO.setDepLatitude(depLatitude);
//            forecastPriceDTO.setDestLongitude(destLongitude);
//            forecastPriceDTO.setDestLatitude(destLatitude);

            ResponseResult<DirectionResponse> direction = serviceMapClient.direction(depLongitude, depLatitude, destLongitude, destLatitude);
            Integer distance = direction.getData().getDistance();
            Integer duration = direction.getData().getDuration();
            log.info("距离："+distance);
            log.info("时长："+duration);
            log.info("读取计价规则");

            log.info("根据距离时长和计价规则计算价格");

            ForecastPriceResponse forecastPriceResponse = new ForecastPriceResponse();
            forecastPriceResponse.setPrice(12.34);

            return ResponseResult.success(forecastPriceResponse);
        }
}
