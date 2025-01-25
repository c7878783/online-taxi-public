package com.dsa.servicemap.remote;

import com.dsa.internalcommon.constant.AmapConfigConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.DirectionResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class MapDirectionClient {

    @Value("${amap.key}")
    private String amapKey;

    @Autowired
    private RestTemplate restTemplate;

    public DirectionResponse direction(String depLongitude, String depLatitude, String destLongitude, String destLatitude){

        //组装请求调用url
        //?origin=116.481028,39.989643&destination=116.465302,40.004717&extensions=all&output=json&key=9d6c50bd7e7e07a18f22be091534c47a
        StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(AmapConfigConstants.DIRECTIONURL)
                .append("?")
                .append("origin="+depLongitude+","+depLatitude)
                .append("&")
                .append("destination="+destLongitude+","+destLatitude)
                .append("&")
                .append("extensions=base")
                .append("&")
                .append("output=json")
                .append("&")
                .append("key="+amapKey);
        log.info(urlBuilder.toString());
        //调用高德接口
        ResponseEntity<String> directionEntity = restTemplate.getForEntity(urlBuilder.toString(), String.class);
        String directionString = directionEntity.getBody();

        log.info("高德地图路径规划返回信息"+directionString);
        DirectionResponse directionResponse = parseDirectionEntity(directionString);
        //解析接口

        return directionResponse;
    }

    private DirectionResponse parseDirectionEntity(String directionString){
        DirectionResponse directionResponse = null;

        try {
            JSONObject result = JSONObject.fromObject(directionString);
            if (result.has(AmapConfigConstants.STATUS)){
                if (1 == result.getInt(AmapConfigConstants.STATUS)) {
                    if (result.has(AmapConfigConstants.ROUTE)){
                        JSONObject routeObject = result.getJSONObject(AmapConfigConstants.ROUTE);
                        JSONArray pathsArray = routeObject.getJSONArray(AmapConfigConstants.PATHS);
                        JSONObject pathObject = pathsArray.getJSONObject(0);//取第一个方案
                        directionResponse = new DirectionResponse();
                        if (pathObject.has(AmapConfigConstants.DISTANCE)){
                            int distance = pathObject.getInt(AmapConfigConstants.DISTANCE);
                            directionResponse.setDistance(distance);
                        }
                        if (pathObject.has(AmapConfigConstants.DURATION)){
                            int duration = pathObject.getInt(AmapConfigConstants.DURATION);
                            directionResponse.setDuration(duration);
                        }
                    };
                }
            }
        } catch (Exception e) {

        }


        return directionResponse;
    }

}
