package com.dsa.servicemap.remote;

import com.dsa.internalcommon.constant.AmapConfigConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MapDicDistrictClient {

    @Value("${amap.key}")
    private String amapKey;

    @Autowired
    RestTemplate restTemplate;

    public String dicDistrict(String keywords) {
        //拼装请求url
        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstants.DISTRICT_URL)
                .append("?")
                .append("keywords="+keywords)
                .append("&")
                .append("subdistrict=3")
                .append("&")
                .append("key="+amapKey);
        //解析结果
        ResponseEntity<String> forEntity = restTemplate.getForEntity(url.toString(), String.class);

        //插入数据库

        return forEntity.getBody();
    }

}
