package com.dsa.servicemap.service;

import com.dsa.internalcommon.constant.AmapConfigConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class DicDistrictService {

    @Value("${amap.key}")
    private String amapKey;

    public ResponseResult initDistrict(String keywords) {
        //拼装请求url
        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstants.DISTRICT_URL)
                .append("?")
                .append("keywords+"+keywords)
                .append("&")
                .append("subdistrict=3")
                .append("&")
                .append("key="+amapKey);
        //解析结果

        //插入数据库
        return null;

    }
}
