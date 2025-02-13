package com.dsa.servicedriveruser.service;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.DriverUserWorkStatus;
import com.dsa.servicedriveruser.mapper.DriverUserWorkStatusMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DriverUserWorkStatusService {

    @Autowired
    DriverUserWorkStatusMapper driverUserWorkStatusMapper;

    public ResponseResult changeWorkStatus(Long driverId, Integer workStatus){
        LocalDateTime now = LocalDateTime.now();
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", driverId);

        List<DriverUserWorkStatus> driverUserWorkStatuses = driverUserWorkStatusMapper.selectByMap(map);
        DriverUserWorkStatus driverUserWorkStatus = driverUserWorkStatuses.get(0);
        driverUserWorkStatus.setWorkStatus(workStatus);
        driverUserWorkStatus.setGmtModified(now);
        driverUserWorkStatusMapper.updateById(driverUserWorkStatus);
        return ResponseResult.success("");
    }

    public ResponseResult<DriverUserWorkStatus> getWorkStatus(Long driverId) {
        Map<String,Object> queryMap = new HashMap<>();
        queryMap.put("driver_id",driverId);
        List<DriverUserWorkStatus> driverUserWorkStatuses = driverUserWorkStatusMapper.selectByMap(queryMap);
        DriverUserWorkStatus driverUserWorkStatus = driverUserWorkStatuses.get(0);

        return ResponseResult.success(driverUserWorkStatus);

    }
}
