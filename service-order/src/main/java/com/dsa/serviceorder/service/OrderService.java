package com.dsa.serviceorder.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dsa.internalcommon.constant.CommonStatusEnum;
import com.dsa.internalcommon.constant.IdentityConstants;
import com.dsa.internalcommon.constant.OrderConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Car;
import com.dsa.internalcommon.pojo.Order;
import com.dsa.internalcommon.request.OrderRequest;
import com.dsa.internalcommon.responese.OrderDriverResponse;
import com.dsa.internalcommon.responese.TerminalResponse;
import com.dsa.internalcommon.util.RedisPrefixUtils;
import com.dsa.serviceorder.mapper.OrderMapper;
import com.dsa.serviceorder.remote.ServiceClient;
import com.dsa.serviceorder.remote.ServiceDriverUserClient;
import com.dsa.serviceorder.remote.ServiceMapClient;
import com.dsa.serviceorder.remote.ServiceSsePushClient;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
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
    private ServiceSsePushClient serviceSsePushClient;

    @Autowired
    RedissonClient redissonClient;


//    @Autowired
//    private SqlSessionTemplate sqlSessionTemplate;
//
//    @Autowired
//    private RedisTemplate<Object, Object> redisTemplate;

    /**
     * 由乘客发起新建订单
     * @param orderRequest
     * @return
     */
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
        for (int i = 0; i < 6; i++) {
            int result = dispatchRealTimeOrder(order);
            if (result == 1){
                //派单成功
                break;
            }
            try {
                //等待20s
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return ResponseResult.success("");
    }

    /**
     * 实时订单派单逻辑
     * @param order
     * @return
     */
    //这里加synchronized只是jvm级别的，当启动两个相同的微服务时，锁会失效，所以我们使用redis
//    public synchronized void dispatchRealTimeOrder(Order order){
    public int dispatchRealTimeOrder(Order order){
        log.info("循环一次");
        int result = 0;
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
                    Long driverId = availableDriver.getData().getDriverId();
                    String lockKey = (driverId + "").intern();
                    RLock lock = redissonClient.getLock(lockKey);
                    lock.lock();
                    Long validOrderNum = ifDriverOrderGoingOn(driverId);
                    if (validOrderNum > 0){
                        lock.unlock();//不加这行，直接跳走导致死锁
                        continue ;//继续找车
                    }
                    //订单直接匹配司机
                    //查询当前车辆信息
                    QueryWrapper<Object> carQW = new QueryWrapper<>();
                    carQW.eq("id", carId);

                    //查询当前司机信息
                    order.setDriverId(driverId);
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

                    //通知司机
                    JSONObject driverContent = new JSONObject();
                    driverContent.put("passengerId", order.getPassengerId());
                    driverContent.put("passengerPhone", order.getPassengerPhone());

                    driverContent.put("depature", order.getDeparture());
                    driverContent.put("depLongitude", order.getDepLongitude());
                    driverContent.put("depLatitude", order.getDepLatitude());

                    driverContent.put("destination", order.getDestination());
                    driverContent.put("destLongitude", order.getDestLongitude());
                    driverContent.put("destLatitude", order.getDestLatitude());

                    serviceSsePushClient.push(driverId, IdentityConstants.DRIVER_IDENTITY, driverContent.toString());

                    //通知乘客
                    JSONObject passengerContent = new JSONObject();
                    passengerContent.put("driverId", order.getDriverId());
                    passengerContent.put("driverPhone", order.getDriverPhone());
                    //车辆信息
                    passengerContent.put("vehicleNo", order.getVehicleNo());
                    passengerContent.put("vehicleType", order.getVehicleType());
                    passengerContent.put("receiveOrderCarLongitude", order.getReceiveOrderCarLongitude());
                    passengerContent.put("receiveOrderCarLatitude", order.getReceiveOrderCarLatitude());
                    //车辆信息，需要查表的
                    ResponseResult<Car> carById = serviceDriverUserClient.getCarById(order.getCarId());
                    Car carByIdData = carById.getData();
                    String brand = carByIdData.getBrand();
                    String model = carByIdData.getModel();
                    String vehicleColor = carByIdData.getVehicleColor();
                    passengerContent.put("brand", brand);
                    passengerContent.put("model", model);
                    passengerContent.put("vehicleColor", vehicleColor);
                    serviceSsePushClient.push(order.getPassengerId(), IdentityConstants.PASSENGER_IDENTITY, passengerContent.toString());

                    result = 1;

                    lock.unlock();

                    break radius;
                }
                //查看车辆是否可以派单
                //派单成功，退出循环
            }
        }
    return result;
    }

    /**
     * 查看对应城市是否有计价规则
     * @param fareType
     * @return
     */
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

    /**
     * 查看是否是黑名单
     * @param deviceCode
     * @return
     */
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

    /**
     * 司机去接乘客时上传位置和时间
     * @param orderRequest
     * @return
     */
    public ResponseResult toPickUp(OrderRequest orderRequest) {
        Long orderId = orderRequest.getOrderId();
        LocalDateTime toPickUpPassengerTime = orderRequest.getToPickUpPassengerTime();
        String toPickUpPassengerLongitude = orderRequest.getToPickUpPassengerLongitude();
        String toPickUpPassengerLatitude = orderRequest.getToPickUpPassengerLatitude();
        String toPickUpPassengerAddress = orderRequest.getToPickUpPassengerAddress();
        QueryWrapper<Order> QW = new QueryWrapper<>();
        QW.eq("id", orderId);
        Order order = orderMapper.selectOne(QW);

        order.setToPickUpPassengerTime(toPickUpPassengerTime);
        order.setToPickUpPassengerLongitude(toPickUpPassengerLongitude);
        order.setToPickUpPassengerLatitude(toPickUpPassengerLatitude);
        order.setToPickUpPassengerAddress(toPickUpPassengerAddress);
        order.setOrderStatus(OrderConstants.DRIVER_TO_PICK_UP_PASSENGER);

        orderMapper.updateById(order);
        return ResponseResult.success("出发去接乘客");
    }

    /**
     * 司机抵达上车点
     * @param orderRequest
     * @return
     */
    public ResponseResult arrivedDeparture(OrderRequest orderRequest) {
        Long orderId = orderRequest.getOrderId();
        QueryWrapper<Order> QW = new QueryWrapper<>();
        QW.eq("id", orderId);

        Order order = orderMapper.selectOne(QW);
        order.setOrderStatus(OrderConstants.DRIVER_ARRIVED_DEPARTURE);

        order.setDriverArrivedDepartureTime(LocalDateTime.now());
        orderMapper.updateById(order);
        return ResponseResult.success("司机抵达上车点");
    }

    /**
     * 乘客上车，行程开始
     * @param orderRequest
     * @return
     */
    public ResponseResult pickUp(OrderRequest orderRequest) {
        Long orderId = orderRequest.getOrderId();
        QueryWrapper<Order> QW = new QueryWrapper<>();
        QW.eq("id", orderId);
        Order order = orderMapper.selectOne(QW);
        order.setOrderStatus(OrderConstants.PICK_UP_PASSENGER);
        order.setPickUpPassengerLongitude(orderRequest.getPickUpPassengerLongitude());
        order.setPickUpPassengerLatitude(orderRequest.getPickUpPassengerLatitude());
        order.setPickUpPassengerTime(LocalDateTime.now());

        orderMapper.updateById(order);
        return ResponseResult.success("已接到乘客，形成开始");
    }

    /**
     * 到达目的地
     * @param orderRequest
     * @return
     */
    public ResponseResult passengerGetoff(OrderRequest orderRequest) {
        Long orderId = orderRequest.getOrderId();
        QueryWrapper<Order> QW = new QueryWrapper<>();
        QW.eq("id", orderId);
        Order order = orderMapper.selectOne(QW);
        order.setOrderStatus(OrderConstants.PASSENGER_GETOFF);
        order.setPassengerGetoffTime(LocalDateTime.now());
        order.setPassengerGetoffLongitude(orderRequest.getPassengerGetoffLongitude());
        order.setPassengerGetoffLatitude(orderRequest.getPassengerGetoffLatitude());
        orderMapper.updateById(order);
        return ResponseResult.success("到达目的地");
    }
}
