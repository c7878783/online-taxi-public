package com.dsa.servicedriveruser.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dsa.internalcommon.constant.CommonStatusEnum;
import com.dsa.internalcommon.constant.DriverCarRelationshipConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.DriverCarBindingRelationship;
import com.dsa.servicedriveruser.mapper.DriverCarBindingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
public class DriverCarBindingService {

    @Autowired
    DriverCarBindingMapper driverCarBindingMapper;

    public ResponseResult bind(DriverCarBindingRelationship driverCarBindingRelationship) {

        //如果已绑定则不允许重复绑定
        QueryWrapper<DriverCarBindingRelationship> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq("driver_id", driverCarBindingRelationship.getDriverId());
        queryWrapper.eq("car_id", driverCarBindingRelationship.getCarId());
        queryWrapper.eq("binding_state", DriverCarRelationshipConstants.DRIVER_CAR_BIND);
        Long selected = driverCarBindingMapper.selectCount(queryWrapper);
        if((selected) > 0){
            return ResponseResult.fail(CommonStatusEnum.DRIVER_CAR_BIND_EXISTS.getCode(), CommonStatusEnum.DRIVER_CAR_BIND_EXISTS.getValue());
        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("driver_id", driverCarBindingRelationship.getDriverId());
        queryWrapper.eq("binding_state", DriverCarRelationshipConstants.DRIVER_CAR_BIND);
        selected = driverCarBindingMapper.selectCount(queryWrapper);
        if((selected) > 0){
            return ResponseResult.fail(CommonStatusEnum.DRIVER_BIND_EXISTS.getCode(), CommonStatusEnum.DRIVER_BIND_EXISTS.getValue());
        }
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("car_id", driverCarBindingRelationship.getCarId());
        queryWrapper.eq("binding_state", DriverCarRelationshipConstants.DRIVER_CAR_BIND);
        selected = driverCarBindingMapper.selectCount(queryWrapper);
        if((selected) > 0){
            return ResponseResult.fail(CommonStatusEnum.CAR_BIND_EXISTS.getCode(), CommonStatusEnum.CAR_BIND_EXISTS.getValue());
        }

        LocalDateTime now = LocalDateTime.now();

        driverCarBindingRelationship.setBindingTime(now);
        driverCarBindingRelationship.setBindingState(DriverCarRelationshipConstants.DRIVER_CAR_BIND);
        driverCarBindingMapper.insert(driverCarBindingRelationship);

        return ResponseResult.success();
    }

    public ResponseResult unbind(DriverCarBindingRelationship driverCarBindingRelationship) {
        LocalDateTime now = LocalDateTime.now();

        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_id", driverCarBindingRelationship.getDriverId());
        map.put("car_id", driverCarBindingRelationship.getCarId());
        map.put("binding_state", DriverCarRelationshipConstants.DRIVER_CAR_BIND);
        List<DriverCarBindingRelationship> driverCarBindingRelationships = driverCarBindingMapper.selectByMap(map);
        if (driverCarBindingRelationships.isEmpty()){
            return ResponseResult.fail(CommonStatusEnum.DRIVER_CAR_BIND_NOT_EXISTS.getCode(), CommonStatusEnum.DRIVER_CAR_BIND_NOT_EXISTS.getValue());
        }
        DriverCarBindingRelationship relationship = driverCarBindingRelationships.get(0);
        relationship.setBindingState(DriverCarRelationshipConstants.DRIVER_CAR_UNBIND);
        relationship.setUnBindingTime(now);
        driverCarBindingMapper.updateById(relationship);
        return ResponseResult.success();
    }
}
