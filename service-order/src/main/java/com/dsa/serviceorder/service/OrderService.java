package com.dsa.serviceorder.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dsa.internalcommon.constant.CommonStatusEnum;
import com.dsa.internalcommon.constant.OrderConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Order;
import com.dsa.internalcommon.request.OrderRequest;
import com.dsa.internalcommon.responese.OrderDriverResponse;
import com.dsa.internalcommon.responese.TerminalResponse;
import com.dsa.internalcommon.util.RedisPrefixUtils;
import com.dsa.serviceorder.mapper.OrderMapper;
import com.dsa.serviceorder.remote.ServiceClient;
import com.dsa.serviceorder.remote.ServiceDriverUserClient;
import com.dsa.serviceorder.remote.ServiceMapClient;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    ServiceClient serviceClient;

    @Autowired
    ServiceDriverUserClient serviceDriverUserClient;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ServiceMapClient serviceMapClient;

    @Autowired
    RedissonClient redissonClient;
//    @Autowired
//    private SqlSessionTemplate sqlSessionTemplate;
//
//    @Autowired
//    private RedisTemplate<Object, Object> redisTemplate;

    public ResponseResult add(OrderRequest orderRequest){
        LocalDateTime now = LocalDateTime.now();
        //下单的城市和车型是否支持业务
        if (!ifPriceRuleExists(orderRequest.getFareType())){
            return ResponseResult.fail(CommonStatusEnum.CITY_NOT_SERVICE.getCode(), CommonStatusEnum.CITY_NOT_SERVICE.getValue());
        }
        //下单的城市是否有司机
        if (!serviceDriverUserClient.isAvailableDriver(orderRequest.getAddress()).getData()){
            return ResponseResult.fail(CommonStatusEnum.CITY_DRIVER_EMPTY.getCode(), CommonStatusEnum.CITY_DRIVER_EMPTY.getValue());
        }
        //判断下单设备是否是黑名单设备
        if (isBlackDevice(orderRequest.getDeviceCode())){
            return ResponseResult.fail(CommonStatusEnum.DEVICE_IS_BLACK.getCode(), CommonStatusEnum.DEVICE_IS_BLACK.getValue());
        }
        //判断乘客是否有正在进行中的订单
        Long validOrderNum = ifPassengerOrderGoingOn(orderRequest.getPassengerId());
        if (validOrderNum > 0){
            return ResponseResult.fail(CommonStatusEnum.ORDER_EXISTS.getCode(), CommonStatusEnum.ORDER_EXISTS.getValue());
        }
        //判断计价规则是不是最新
        ResponseResult<Boolean> isNew = serviceClient.isNew(orderRequest.getFareType(), orderRequest.getFareVersion());
        if (!isNew.getData()){
            return ResponseResult.fail(CommonStatusEnum.PRICE_RULE_CHANGED.getCode(),CommonStatusEnum.PRICE_RULE_CHANGED.getValue());
        }

        //创建订单
        Order order = new Order();

        BeanUtils.copyProperties(orderRequest, order);

        order.setOrderStatus(OrderConstants.ORDER_START);
        order.setFareVersion(orderRequest.getFareVersion());
        order.setGmtCreate(now);
        order.setGmtModified(now);
        orderMapper.insert(order);
        //实时寻找附近司机,附近存在司机才可继续
//        dispatchRealTimeOrder(order);
        return ResponseResult.success("");
    }
    //实时订单派单逻辑
    //这里加synchronized只是jvm级别的，当启动两个相同的微服务时，锁会失效，所以我们使用redis
//    public synchronized void dispatchRealTimeOrder(Order order){
    public void dispatchRealTimeOrder(Order order){
        //两公里
        String depLongitude = order.getDepLongitude();
        String depLatitude = order.getDepLatitude();

        //定义搜索半径列表
        ArrayList<Integer> radiusList = new ArrayList<>();
        radiusList.add(2000);
        radiusList.add(4000);
        radiusList.add(5000);
        ResponseResult<List<TerminalResponse>> listResponseResult = null;
        String center = depLatitude + "," + depLongitude;
        radius://goto语法
        for (int i = 0; i < radiusList.size(); i++) {
            listResponseResult = serviceMapClient.aroundSearch(center, radiusList.get(i));

            log.info("寻找车辆半径：" + radiusList.get(i));
            List<TerminalResponse> data = listResponseResult.getData();
            for (int j = 0; j < data.size(); j++) {
                TerminalResponse terminalResponse = data.get(j);
                Long carId = terminalResponse.getCarId();
                String longitude = terminalResponse.getLongitude();
                String latitude = terminalResponse.getLatitude();

                log.info("找到车辆："+carId);
                //查询车辆信息
                ResponseResult<OrderDriverResponse> availableDriver = serviceDriverUserClient.getAvailableDriver(carId);
                if (availableDriver.getCode() == CommonStatusEnum.AVAILABLE_DRIVER_EMPTY.getCode()){
                    continue radius;//跳到radius
                }else {
                    log.info("找到了正在出车的司机："+carId);
                    //判断司机是否有正在进行中的订单
                    //这个部分在高并发下会产生问题，很可能刚查出来司机没有订单，但是同时别人就把这个司机抢走了
                    String lockKey = (availableDriver.getData().getDriverId() + "").intern();
                    RLock lock = redissonClient.getLock(lockKey);
                    lock.lock();
                    Long validOrderNum = ifDriverOrderGoingOn(availableDriver.getData().getDriverId());
                    if (validOrderNum > 0){
                        lock.unlock();//不加这行，直接跳走导致死锁
                        continue ;//继续找车
                    }
                    //订单直接匹配司机
                    //查询当前车辆信息
                    QueryWrapper<Object> carQW = new QueryWrapper<>();
                    carQW.eq("id", carId);

                    //查询当前司机信息
                    order.setDriverId(availableDriver.getData().getDriverId());
                    order.setDriverPhone(availableDriver.getData().getDriverPhone());
                    order.setCarId(availableDriver.getData().getCarId());
                    //当前时间
                    LocalDateTime now = LocalDateTime.now();
                    order.setReceiveOrderTime(now);
                    //地图中来
                    order.setReceiveOrderCarLongitude(longitude);
                    order.setReceiveOrderCarLatitude(latitude);
                    //从司机和车辆来
                    order.setLicenseId(availableDriver.getData().getLicenseId());
                    order.setVehicleNo(availableDriver.getData().getVehicleNo());
                    order.setVehicleType(availableDriver.getData().getVehicleType());
                    order.setOrderStatus(OrderConstants.DRIVER_RECEIVE_ORDER);

                    order.setGmtModified(now);
                    orderMapper.updateById(order);

                    lock.unlock();

                    break radius;
                }
                //查看车辆是否可以派单
                //派单成功，退出循环
            }
        }


    }

    private boolean ifPriceRuleExists(String fareType){
        int $ = fareType.indexOf("$");
        String cityCode = fareType.substring(0, $);
        String vehicleType = fareType.substring($ + 1);
        ResponseResult<Boolean> booleanResponseResult = serviceClient.ifExists(cityCode, vehicleType);
        return booleanResponseResult.getData();
    }

    /**
     * 乘客订单是否在进行，即订单状态1-7
     * @param passengerId
     * @return
     */
    private Long ifPassengerOrderGoingOn(Long passengerId){
        //判断有正在进行的订单（订单状态1~7）不允许下单
        QueryWrapper<Order> queryWrapper = new QueryWrapper();
        queryWrapper.eq("passenger_id", passengerId);
        queryWrapper.and(wrapper -> wrapper.eq("order_status", OrderConstants.ORDER_START)
                .or().eq("order_status", OrderConstants.DRIVER_RECEIVE_ORDER)
                .or().eq("order_status", OrderConstants.DRIVER_TO_PICK_UP_PASSENGER)
                .or().eq("order_status", OrderConstants.DRIVER_ARRIVED_DEPARTURE)
                .or().eq("order_status", OrderConstants.PICK_UP_PASSENGER)
                .or().eq("order_status", OrderConstants.PASSENGER_GETOFF)
                .or().eq("order_status", OrderConstants.TO_START_PAY));

        Long validOrderNum = orderMapper.selectCount(queryWrapper);
        return validOrderNum;
    }

    /**
     * 司机订单是否在进行，即订单状态2-5
     * @param driverId
     * @return
     */
    private Long ifDriverOrderGoingOn(Long driverId){
        QueryWrapper<Order> queryWrapper = new QueryWrapper();
        queryWrapper.eq("driver_id", driverId);
        queryWrapper.and(wrapper -> wrapper.eq("order_status", OrderConstants.DRIVER_RECEIVE_ORDER)
                .or().eq("order_status", OrderConstants.DRIVER_TO_PICK_UP_PASSENGER)
                .or().eq("order_status", OrderConstants.DRIVER_ARRIVED_DEPARTURE)
                .or().eq("order_status", OrderConstants.PICK_UP_PASSENGER));//下一个状态是到达目的地乘客下车，从这里开始就可以再接单了

        Long validOrderNum = orderMapper.selectCount(queryWrapper);
        log.info("司机"+driverId+"正在进行的订单数量:"+validOrderNum);
        return validOrderNum;
    }


    private boolean isBlackDevice(String deviceCode){
        String deviceCodeKey = RedisPrefixUtils.blackDeviceCodePrefix + deviceCode;
        Boolean b = stringRedisTemplate.hasKey(deviceCodeKey);
        if (b){
            String s = stringRedisTemplate.opsForValue().get(deviceCodeKey);
            int i = Integer.parseInt(s);
            if (i >= 2 ){
                //当前设备超过下单次数
                return true;
            }else {
                stringRedisTemplate.opsForValue().increment(deviceCodeKey);
            }
        }else {
            stringRedisTemplate.opsForValue().setIfAbsent(deviceCodeKey, "1", 1L, TimeUnit.MINUTES);
        }
        return false;
    }

}
