package com.dsa.apidriver.service;

import com.dsa.apidriver.remote.ServiceDriverUserClient;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Car;
import com.dsa.internalcommon.request.ApiDriverPointRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.geom.RectangularShape;

@Service
public class PointService {
    @Autowired
    ServiceDriverUserClient serviceDriverUserClient;

    public ResponseResult upload(ApiDriverPointRequest apiDriverPointRequest){
        //获取carId;
        Long carId = apiDriverPointRequest.getCarId();
        //通过carId 获取tid、trid、调用service-driveuser的接口
        ResponseResult<Car> carById = serviceDriverUserClient.getCarById(carId);
        Car car = carById.getData();
        String tid = car.getTid();
        String trid = car.getTrid();
        //调用地图去上传

        return null;
    }
}
