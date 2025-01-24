package com.dsa.serviceprice.service;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.ForecastPriceResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ForecastPriceService {

        public ResponseResult forecastPrice(String depLongitude, String depLatitude, String destLongitude, String destLatitude){
            log.info("出发地经度：" + depLongitude);
            log.info("出发地纬度：" + depLatitude);
            log.info("目的地经度：" + destLongitude);
            log.info("目的地纬度：" + destLatitude);


            log.info("调用地图服务，查询距离和时长");

            log.info("读取计价规则");

            log.info("根据距离时长和计价规则计算价格");

            ForecastPriceResponse forecastPriceResponse = new ForecastPriceResponse();
            forecastPriceResponse.setPrice(12.34);

            return ResponseResult.success(forecastPriceResponse);
        }
}
