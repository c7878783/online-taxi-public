package com.dsa.servicemap.remote;

import com.dsa.internalcommon.constant.AmapConfigConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.TerminalResponse;
import com.dsa.internalcommon.responese.TrsearchResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TerminalClient {

    @Value("${amap.key}")
    private String amapKey;

    @Value("${amap.sid}")
    private String sid;

    @Autowired
    RestTemplate restTemplate;

    public ResponseResult add(String name, String desc){
        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstants.TERMINAL_ADD_URL)
                .append("?")
                .append("key="+amapKey)
                .append("&")
                .append("sid="+sid)
                .append("&")
                .append("name="+name)
                .append("&")
                .append("desc="+desc)
        ;
        log.info(url.toString());
        ResponseEntity<String> forEntity = restTemplate.postForEntity(url.toString(), null,String.class);
        String body = forEntity.getBody();
        JSONObject jsonObject = JSONObject.fromObject(body);
        //        {
//            "errcode": 10000,
//                "errmsg": "OK",
//                "data": {
//                      "name": "车辆1",
//                      "tid": 1163709601,
//                      "sid": 1044731
//        }
//        }
        JSONObject data = jsonObject.getJSONObject("data");
        String tid = data.getString("tid");
        TerminalResponse terminalResponse = new TerminalResponse();
        terminalResponse.setTid(tid);

        return ResponseResult.success(terminalResponse);
    }

    public ResponseResult<List<TerminalResponse>> aroundSearch(String center, Integer radius){
        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstants.TERMINAL_AROUND_SEARCH)
                .append("?")
                .append("key="+amapKey)
                .append("&")
                .append("sid="+sid)
                .append("&")
                .append("center="+center)
                .append("&")
                .append("radius="+radius)
        ;
        log.info(url.toString());
        ResponseEntity<String> forEntity = restTemplate.postForEntity(url.toString(), null,String.class);
        String body = forEntity.getBody();
        JSONObject jsonObject = JSONObject.fromObject(body);
        JSONObject data = jsonObject.getJSONObject("data");

        ArrayList<TerminalResponse> terminalResponseArrayList = new ArrayList<>();
        JSONArray jsonArray = data.getJSONArray("results");
        for (int i = 0; i < jsonArray.size(); i++) {
            TerminalResponse terminalResponse = new TerminalResponse();
            JSONObject results = jsonArray.getJSONObject(i);
            Long carId = results.getLong("desc");
            String tid = results.getString("tid");
            terminalResponse.setCarId(carId);
            terminalResponse.setTid(tid);

            JSONObject location = results.getJSONObject("location");
            String longitude = location.getString("longitude");
            String latitude = location.getString("latitude");
            terminalResponse.setLongitude(longitude);
            terminalResponse.setLatitude(latitude);
            terminalResponseArrayList.add(terminalResponse);
        }
//        {
//            "errcode": 10000,
//                "errmsg": "OK",
//                "data": {
//            "results": [
//            {
//                "tid": 1163951505,
//                    "name": "沪B89999",
//                    "desc": "11",
//                    "createtime": 1738310598747,
//                    "locatetime": 1738310804329,
//                    "location": {
//                "latitude": 39.22,
//                        "longitude": 116.44,
//                        "speed": 255.0,
//                        "direction": 511.0,
//                        "accuracy": 550.0,
//                        "distance": 0
//            }
//            }
//        ],
//            "count": 1
//        }
//        }
        return ResponseResult.success(terminalResponseArrayList);
    }

    public ResponseResult<TrsearchResponse> trsearch(String tid, Long starttime, Long endtime) {
        StringBuilder url = new StringBuilder();
        url.append(AmapConfigConstants.TERMINAL_TRACE_SEARCH)
                .append("?")
                .append("key="+amapKey)
                .append("&")
                .append("sid="+sid)
                .append("&")
                .append("tid="+tid)
                .append("&")
                .append("starttime="+starttime)
                .append("&")
                .append("endtime="+endtime)
        ;
        log.info(url.toString());
        ResponseEntity<String> forEntity = restTemplate.postForEntity(url.toString(), null,String.class);
        String body = forEntity.getBody();
        JSONObject jsonObject = JSONObject.fromObject(body);
        JSONObject data = jsonObject.getJSONObject("data");

        int counts = data.getInt("counts");
        if (counts == 0){
            return ResponseResult.success(null);
        }
        JSONArray tracks = data.getJSONArray("tracks");
        long driverMile = 0L;
        long driverTime = 0L;
        for (int i = 0; i <tracks.size(); i++) {
            JSONObject track = tracks.getJSONObject(i);
            long distanceMile = track.getLong("distance");
            long timeMs = track.getLong("time");
            long timeMin = timeMs / (1000 * 60);

            driverMile = driverMile + distanceMile;
            driverTime = driverTime + timeMin;
        }
        TrsearchResponse trsearchResponse = new TrsearchResponse();
        trsearchResponse.setDistanceMile(driverMile);
        trsearchResponse.setTimeMin(driverTime);
//        {
//            "errcode": 10000,
//                "errmsg": "OK",
//                "data": {
//            "tracks": [
//            {
//                "startPoint": {
//                "location": "114.555334,38.031267",
//                        "locatetime": 1738589269389
//            },
//                "endPoint": {
//                "location": "114.511826,38.043172",
//                        "locatetime": 1738589427505
//            },
//                "trid": 340,
//                    "distance": 5088,
//                    "time": 158125,
//                    "counts": 8,
//                    "points": [
//                这地方是一串points，节约空间省略了
//        ],
//            "counts": 1
//        }
//        }
        return ResponseResult.success(trsearchResponse);
    }
}
