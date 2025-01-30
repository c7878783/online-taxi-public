package com.dsa.servicemap.remote;

import com.dsa.internalcommon.constant.AmapConfigConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.PointDTO;
import com.dsa.internalcommon.request.PointRequest;
import com.dsa.internalcommon.responese.TraceResponse;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
public class PointClient {

    @Value("${amap.key}")
    private String amapKey;

    @Value("${amap.sid}")
    private String sid;

    @Autowired
    RestTemplate restTemplate;

    public ResponseResult<TraceResponse> upload(PointRequest pointRequest){
        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstants.POINT_UPLOAD_URL)
                .append("?")
                .append("key="+amapKey)
                .append("&")
                .append("sid="+sid)
                .append("&")
                .append("tid="+pointRequest.getTid())
                .append("&")
                .append("trid="+pointRequest.getTrid())
                .append("&")
                .append("points=")
        ;
        url.append("%5B");  // 先加上 [ 的编码
        for (PointDTO point : pointRequest.getPoints()) {
            url.append("%7B");
            String location = point.getLocation();
            String locatetime = point.getLocatetime();
            url.append("%22location%22");
            url.append("%3A");
            url.append("%22"+location+"%22");
            url.append("%2C");

            url.append("%22locatetime%22");
            url.append("%3A");
            url.append(locatetime);

            url.append("%7D");
        }
        url.append("%5D");

        System.out.println("高德地图请求url: " + url);
        ResponseEntity<String> forEntity = restTemplate.postForEntity(URI.create(url.toString()), null,String.class);
        String body = forEntity.getBody();
        System.out.println("高德地图响应: " + body);


        return ResponseResult.success();
    }
}
