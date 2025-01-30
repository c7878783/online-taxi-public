package com.dsa.servicemap.remote;

import com.dsa.internalcommon.constant.AmapConfigConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.TerminalResponse;
import com.dsa.internalcommon.responese.TraceResponse;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TraceClient {

    @Value("${amap.key}")
    private String amapKey;

    @Value("${amap.sid}")
    private String sid;

    @Autowired
    RestTemplate restTemplate;

    public ResponseResult<TraceResponse> add(String tid){
        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstants.TRACE_ADD_URL)
                .append("?")
                .append("key="+amapKey)
                .append("&")
                .append("sid="+sid)
                .append("&")
                .append("tid="+tid)
        ;
        ResponseEntity<String> forEntity = restTemplate.postForEntity(url.toString(), null,String.class);
        String body = forEntity.getBody();
        JSONObject jsonObject = JSONObject.fromObject(body);

//        {"errcode": 10000,
//                  "errmsg": "OK",
//                  "data": {
//                  "trid": 120
//        }
//
        JSONObject data = jsonObject.getJSONObject("data");
        String trid = data.getString("trid");
        TraceResponse traceResponse = new TraceResponse();
        traceResponse.setTrid(trid);
        if (data.has("trname")){
            String trname = data.getString("trname");
            traceResponse.setTrname(trname);
        }

        return ResponseResult.success(traceResponse);
    }

}
