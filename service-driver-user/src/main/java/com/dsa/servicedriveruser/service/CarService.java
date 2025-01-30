package com.dsa.servicedriveruser.service;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Car;
import com.dsa.internalcommon.responese.TerminalResponse;
import com.dsa.internalcommon.responese.TraceResponse;
import com.dsa.servicedriveruser.mapper.CarMapper;
import com.dsa.servicedriveruser.remote.ServiceMapClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CarService {

    @Autowired
    private CarMapper carMapper;
    @Autowired
    private ServiceMapClient serviceMapClient;

    public ResponseResult addCar(Car car){
        LocalDateTime now = LocalDateTime.now();

        car.setGmtCreate(now);
        car.setGmtModified(now);
        //获取车辆对应tid
        ResponseResult<TerminalResponse> terminalResponseResponseResult = serviceMapClient.addTerminal(car.getVehicleNo());
        String tid = terminalResponseResponseResult.getData().getTid();
        car.setTid(tid);

        //获得此车辆轨迹id：trid
        ResponseResult<TraceResponse> traceResponseResponseResult = serviceMapClient.addTrace(tid);
        String trid = traceResponseResponseResult.getData().getTrid();
        String trname = traceResponseResponseResult.getData().getTrname();
        car.setTrid(trid);
        car.setTrname(trname);

        carMapper.insert(car);

        return ResponseResult.success();
    }

}
