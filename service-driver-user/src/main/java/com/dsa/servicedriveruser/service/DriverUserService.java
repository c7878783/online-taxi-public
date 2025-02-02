package com.dsa.servicedriveruser.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dsa.internalcommon.constant.CommonStatusEnum;
import com.dsa.internalcommon.constant.DriverCarConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.DriverCarBindingRelationship;
import com.dsa.internalcommon.pojo.DriverUser;
import com.dsa.internalcommon.pojo.DriverUserWorkStatus;
import com.dsa.internalcommon.responese.OrderDriverResponse;
import com.dsa.servicedriveruser.mapper.DriverCarBindingMapper;
import com.dsa.servicedriveruser.mapper.DriverUserMapper;
import com.dsa.servicedriveruser.mapper.DriverUserWorkStatusMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
public class DriverUserService {

    @Autowired
    private DriverUserMapper driverUserMapper;
    @Autowired
    private DriverUserWorkStatusMapper driverUserWorkStatusMapper;
    @Autowired
    private DriverCarBindingMapper driverCarBindingMapper;

    public ResponseResult testGetDriverUser(){
        DriverUser driverUser = driverUserMapper.selectById(1);

        return ResponseResult.success(driverUser);
    }

    public ResponseResult addDriverUser(DriverUser driverUser) {
        LocalDateTime now = LocalDateTime.now();
        driverUser.setGmtCreate(now);
        driverUser.setGmtModified(now);
        driverUserMapper.insert(driverUser);
        //初始化司机工作状态
        DriverUserWorkStatus driverUserWorkStatus = new DriverUserWorkStatus();
        driverUserWorkStatus.setDriverId(driverUser.getId());
        driverUserWorkStatus.setWorkStatus(DriverCarConstants.DRIVER_WORK_STATUS_STOP);
        driverUserWorkStatus.setGmtCreate(now);
        driverUserWorkStatus.setGmtModified(now);
        driverUserWorkStatusMapper.insert(driverUserWorkStatus);
        return ResponseResult.success();
    }

    public ResponseResult updateDriverUser(DriverUser driverUser) {
        LocalDateTime now = LocalDateTime.now();
        driverUser.setGmtModified(now);
        driverUserMapper.updateById(driverUser);
        return ResponseResult.success();
    }

    public ResponseResult<DriverUser> getDriverUserByPhone(String  driverPhone){
        HashMap<String, Object> map = new HashMap<>();
        map.put("driver_phone", driverPhone);
        map.put("state", DriverCarConstants.DRIVER_STATE_VALID);
        List<DriverUser> driverUsers = driverUserMapper.selectByMap(map);
        if (driverUsers.isEmpty()){
            return ResponseResult.fail(CommonStatusEnum.DRIVER_NOT_EXISTS.getCode(),CommonStatusEnum.DRIVER_NOT_EXISTS.getValue());
        }
        DriverUser driverUser = driverUsers.get(0);
        return ResponseResult.success(driverUser);
    }

    /**
     * 判断司机车辆是否有对应司机，司机是否出车
     * @param carId
     * @return
     */
    public ResponseResult<OrderDriverResponse> getAvailableDriver(Long carId){
        QueryWrapper<DriverCarBindingRelationship> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("car_id", carId);
        queryWrapper.eq("binding_state", DriverCarConstants.DRIVER_CAR_BIND);

        DriverCarBindingRelationship driverCarBindingRelationship = driverCarBindingMapper.selectOne(queryWrapper);
        Long driverId = driverCarBindingRelationship.getDriverId();

        QueryWrapper<DriverUserWorkStatus> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("driver_id", driverId);
        queryWrapper1.eq("work_status", DriverCarConstants.DRIVER_WORK_STATUS_START);
        DriverUserWorkStatus driverUserWorkStatusCount = driverUserWorkStatusMapper.selectOne(queryWrapper1);

        if (null == driverUserWorkStatusCount){
            return ResponseResult.fail(CommonStatusEnum.AVAILABLE_DRIVER_EMPTY.getCode(),CommonStatusEnum.AVAILABLE_DRIVER_EMPTY.getValue());
        }else {
            QueryWrapper<DriverUser> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("id", driverId);
            DriverUser driverUser = driverUserMapper.selectOne(queryWrapper2);

            OrderDriverResponse orderDriverResponse = new OrderDriverResponse();
            orderDriverResponse.setCarId(carId);
            orderDriverResponse.setDriverId(driverId);
            orderDriverResponse.setDriverPhone(driverUser.getDriverPhone());
            return ResponseResult.success(orderDriverResponse);
        }
    }
}
