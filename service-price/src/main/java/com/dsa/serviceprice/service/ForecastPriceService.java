package com.dsa.serviceprice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dsa.internalcommon.constant.CommonStatusEnum;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.PassengerUser;
import com.dsa.internalcommon.pojo.PriceRule;
import com.dsa.internalcommon.request.ForecastPriceDTO;
import com.dsa.internalcommon.responese.DirectionResponse;
import com.dsa.internalcommon.responese.ForecastPriceResponse;
import com.dsa.internalcommon.util.BigDecimalUtils;
import com.dsa.serviceprice.mapper.PriceRuleMapper;
import com.dsa.serviceprice.remote.ServiceMapClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

@Service
@Slf4j
public class ForecastPriceService {

    @Autowired
    ServiceMapClient serviceMapClient;

    @Autowired
    PriceRuleMapper priceRuleMapper;

    public ResponseResult forecastPrice(String depLongitude, String depLatitude, String destLongitude, String destLatitude,
                                        String cityCode, String vehicleType){
//        log.info("出发地经度：" + depLongitude);
//        log.info("出发地纬度：" + depLatitude);
//        log.info("目的地经度：" + destLongitude);
//        log.info("目的地纬度：" + destLatitude);


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
//        HashMap<String, Object> queryMap = new HashMap<>();
//        queryMap.put("city_code", cityCode);
//        queryMap.put("vehicle_type", vehicleType);
//        List<PriceRule> priceRules = priceRuleMapper.selectByMap(queryMap);
        QueryWrapper<PriceRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("city_code", cityCode);
        queryWrapper.eq("vehicle_type", vehicleType);
        queryWrapper.orderByDesc("fare_version");
        List<PriceRule> priceRules = priceRuleMapper.selectList(queryWrapper);
        if (priceRules.size() == 0){
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_EMPTY.getCode(), CommonStatusEnum.PRICE_RULE_EMPTY.getValue());
        }
        PriceRule priceRule = priceRules.get(0);
        log.info(priceRule.toString());
        log.info("根据距离时长和计价规则计算价格");
        Double price = gerPrice(distance, duration, priceRule);
        ForecastPriceResponse forecastPriceResponse = new ForecastPriceResponse();
        forecastPriceResponse.setPrice(price);
        forecastPriceResponse.setCityCode(cityCode);
        forecastPriceResponse.setVehicleType(vehicleType);
        forecastPriceResponse.setFareType(priceRule.getFareType());
        forecastPriceResponse.setFareVersion(priceRule.getFareVersion());

        return ResponseResult.success(forecastPriceResponse);
    }

    /**
     * 根据距离、时长和计价规则，计算价格
     * @param distance 距离
     * @param duration 时长
     * @param priceRule 计价规则
     * @return 估算价格
     */
//    private Double gerPrice(Integer distance, Integer duration, PriceRule priceRule){
//        BigDecimal price = new BigDecimal(0);
//
//        //起步价
//        Double startFare = priceRule.getStartFare();
//        BigDecimal startFareDecimal = new BigDecimal(startFare);
//        price = price.add(startFareDecimal);
//
//        //里程费
//        BigDecimal distanceDecimal = new BigDecimal(distance);
//        BigDecimal distanceMileDecimal = distanceDecimal.divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP);
//
//        Integer startMile = priceRule.getStartMile();
//        BigDecimal startMileDecimal = new BigDecimal(startMile);
//        double distanceSubtract = distanceMileDecimal.subtract(startMileDecimal).doubleValue();
//        Double mile = distanceSubtract < 0 ? 0 : distanceSubtract;
//        BigDecimal mileDecimal = new BigDecimal(mile);
//
//
//        Double unitPricePerMile = priceRule.getUnitPricePerMile();
//        BigDecimal unitPricePerMileDecimal = new BigDecimal(unitPricePerMile);
//        BigDecimal mileFare = unitPricePerMileDecimal.multiply(mileDecimal).setScale(2, BigDecimal.ROUND_HALF_UP);
//        price = price.add(mileFare);
//
//        //时长费
//        BigDecimal durationDecimal = new BigDecimal(duration);
//        BigDecimal timeDecimal = durationDecimal.divide(new BigDecimal(60), 2, BigDecimal.ROUND_HALF_UP);
//
//        Double unitPricePerMinute = priceRule.getUnitPricePerMinute();
//        BigDecimal unitPricePerMinuteDecimal = new BigDecimal(unitPricePerMinute);
//        BigDecimal timeFare = unitPricePerMinuteDecimal.multiply(timeDecimal).setScale(2, BigDecimal.ROUND_HALF_UP);
//        price = price.add(timeFare);
//
//        return price.doubleValue();
//    }

    private Double gerPrice(Integer distance, Integer duration, PriceRule priceRule){
        double price = 0;
        //起步价
        double startFare = priceRule.getStartFare();
        price = BigDecimalUtils.add(price, startFare);

        //里程费
        double distanceMile = BigDecimalUtils.divide(distance, 1000);
        double startMile = priceRule.getStartMile();

        double distanceSubtract = BigDecimalUtils.subtract(distanceMile, startMile);
        double mile = distanceSubtract < 0 ? 0 : distanceSubtract;


        double unitPricePerMile = priceRule.getUnitPricePerMile();
        double mileFare = BigDecimalUtils.multiply(mile, unitPricePerMile);
        price = BigDecimalUtils.add(price, mileFare);

        //时长费
        double unitPricePerMinute = priceRule.getUnitPricePerMinute();
        double time = BigDecimalUtils.divide(duration, 60);
        double timeFare = BigDecimalUtils.multiply(time, unitPricePerMinute);

        price = BigDecimalUtils.add(price, timeFare);

        return price;
    }

//    public static void main(String[] args) {
//        PriceRule priceRule = new PriceRule();
//        priceRule.setUnitPricePerMile(1.8);
//        priceRule.setUnitPricePerMinute(0.5);
//        priceRule.setStartFare(10.0);
//        priceRule.setStartMile(3);
//
//        System.out.println(gerPrice(6500, 1800, priceRule));
//    }
}
