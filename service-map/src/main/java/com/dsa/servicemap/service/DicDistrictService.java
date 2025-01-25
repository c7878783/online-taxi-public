package com.dsa.servicemap.service;

import com.dsa.internalcommon.constant.AmapConfigConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.servicemap.remote.MapDicDistrictClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DicDistrictService {

    @Value("${amap.key}")
    private String amapKey;

    @Autowired
    MapDicDistrictClient mapDicDistrictClient;



    public ResponseResult initDistrict(String keywords) {
        //请求地图
        String dicDistrict = mapDicDistrictClient.dicDistrict(keywords);
        System.out.println(dicDistrict);


        //解析结果

        //插入数据库
        return ResponseResult.success();

    }
}
