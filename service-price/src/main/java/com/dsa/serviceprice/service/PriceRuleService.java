package com.dsa.serviceprice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dsa.internalcommon.constant.CommonStatusEnum;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.PriceRule;
import com.dsa.serviceprice.mapper.PriceRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.List;

@Service
public class PriceRuleService {

    @Autowired
    PriceRuleMapper priceRuleMapper;

    public ResponseResult add(PriceRule priceRule){

        String cityCode = priceRule.getCityCode();
        String vehicleType = priceRule.getVehicleType();
        String fareType = cityCode + "$" + vehicleType;
        priceRule.setFareType(fareType);

        //添加版本号
        HashMap<String, Object> map = new HashMap<>();
        map.put("city_code", cityCode);
        map.put("vehicle_type", vehicleType);
        //问题1：增加了版本号，前面的两个字段无法确定唯一记录，问题2：找到最大的版本号需要排序
        QueryWrapper<PriceRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("city_code", cityCode);
        queryWrapper.eq("vehicle_type", vehicleType);
        queryWrapper.orderByDesc("fare_version");

        List<PriceRule> priceRules = priceRuleMapper.selectList(queryWrapper);
        Integer fareVersion = 0;
        if (priceRules.size() > 0){
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_EXISTS.getCode(), CommonStatusEnum.PRICE_RULE_EXISTS.getValue());
//            fareVersion = priceRules.get(0).getFareVersion();
        }
        priceRule.setFareVersion(++fareVersion);


        priceRuleMapper.insert(priceRule);

        return ResponseResult.success();
    }

    public ResponseResult update(PriceRule priceRule){

        String cityCode = priceRule.getCityCode();
        String vehicleType = priceRule.getVehicleType();
        String fareType = cityCode + "$" + vehicleType;
        priceRule.setFareType(fareType);

        //添加版本号
        HashMap<String, Object> map = new HashMap<>();
        map.put("city_code", cityCode);
        map.put("vehicle_type", vehicleType);
        //问题1：增加了版本号，前面的两个字段无法确定唯一记录，问题2：找到最大的版本号需要排序
        QueryWrapper<PriceRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("city_code", cityCode);
        queryWrapper.eq("vehicle_type", vehicleType);
        queryWrapper.orderByDesc("fare_version");

        List<PriceRule> priceRules = priceRuleMapper.selectList(queryWrapper);
        Integer fareVersion = 0;
        if (priceRules.size() > 0){
            PriceRule latestRule = priceRules.get(0);
            Double unitPricePerMile = latestRule.getUnitPricePerMile();
            Double unitPricePerMinute = latestRule.getUnitPricePerMinute();
            Double startFare = latestRule.getStartFare();
            Integer startMile = latestRule.getStartMile();
            if (unitPricePerMile.doubleValue() == priceRule.getUnitPricePerMile().doubleValue()
                    && unitPricePerMinute.doubleValue() == priceRule.getUnitPricePerMinute().doubleValue()
                    && startFare.doubleValue() == priceRule.getStartFare().doubleValue()
                    && startMile.doubleValue() == priceRule.getStartMile().doubleValue()
            ){
                return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_NOT_EDIT.getCode(), CommonStatusEnum.PRICE_RULE_NOT_EDIT.getValue());
            }

            fareVersion = latestRule.getFareVersion();
        }
        priceRule.setFareVersion(++fareVersion);


        priceRuleMapper.insert(priceRule);

        return ResponseResult.success();
    }

    public ResponseResult<PriceRule> getNewestVersion(String fareType){
        QueryWrapper<PriceRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fare_type", fareType);
        queryWrapper.orderByDesc("fare_version");

        List<PriceRule> priceRuleList = priceRuleMapper.selectList(queryWrapper);

        if (priceRuleList.size() > 0){
            return ResponseResult.success(priceRuleList.get(0));
        }else {
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_EMPTY.getCode(), CommonStatusEnum.PRICE_RULE_EMPTY.getValue());
        }

    }

    public ResponseResult<Boolean> isNew(String fareType, Integer fareVersion) {
        ResponseResult<PriceRule> newestVersion = getNewestVersion(fareType);
        if (newestVersion.getCode() == CommonStatusEnum.PRICE_RULE_EMPTY.getCode()){
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_EMPTY.getCode(), CommonStatusEnum.PRICE_RULE_EMPTY.getValue());
        }
        if (newestVersion.getData().getFareVersion() == fareVersion){
            return ResponseResult.success(true);
        }else {
            return ResponseResult.success(false);
        }

    }

    public ResponseResult<Boolean> isExists(String cityCode, String vehicleType) {
        QueryWrapper<PriceRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("city_code", cityCode);
        queryWrapper.eq("vehicle_type", vehicleType);
        queryWrapper.orderByDesc("fare_version");
        List<PriceRule> priceRuleList = priceRuleMapper.selectList(queryWrapper);

        if (priceRuleList.size() > 0){
            return ResponseResult.success(true);
        }else{
            return ResponseResult.success(false);
        }

    }
}
