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
import com.dsa.internalcommon.responese.TrsearchResponse;
import com.dsa.internalcommon.util.RedisPrefixUtils;
import com.dsa.serviceorder.mapper.OrderMapper;
import com.dsa.serviceorder.remote.*;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OrderService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    ServicePriceClient servicePriceClient;

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
        ResponseResult<Boolean> isNew = servicePriceClient.isNew(orderRequest.getFareType(), orderRequest.getFareVersion());
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
        //从fareType中得到vehicleType
        HashMap<String, String> cityCodeVehicleType = getCityCodeVehicleType(orderRequest.getFareType());
        order.setVehicleType(cityCodeVehicleType.get("vehicleType"));
        orderMapper.insert(order);
        //实时寻找附近司机,附近存在司机才可继续
        for (int i = 0; i < 6; i++) {
            int result = dispatchRealTimeOrder(order);
            if (result == 1){
                //派单成功
                break;
            }

            if (i == 5){
                //没找到司机，订单无效
                order.setOrderStatus(OrderConstants.ORDER_INVALID);
                orderMapper.updateById(order);
            }else {
                try {
                    //等待20s
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                    OrderDriverResponse availableDriverData = availableDriver.getData();
                    String vehicleTypeFromCar = availableDriverData.getVehicleType();
                    if (!vehicleTypeFromCar.trim().equals(order.getVehicleType())){
                        log.info("找到的车型不匹配："+vehicleTypeFromCar);
                        continue ;
                    }
                    log.info("找到了正在出车的司机："+carId);
                    //判断司机是否有正在进行中的订单
                    //这个部分在高并发下会产生问题，很可能刚查出来司机没有订单，但是同时别人就把这个司机抢走了
                    Long driverId = availableDriverData.getDriverId();
                    String lockKey = (driverId + "").intern();
                    RLock lock = redissonClient.getLock(lockKey);
                    lock.lock();
                    Long validOrderNum = ifDriverOrderGoingOn(driverId);//当前司机是否有有效订单
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
                    order.setDriverPhone(availableDriverData.getDriverPhone());
                    order.setCarId(availableDriverData.getCarId());
                    //当前时间
                    LocalDateTime now = LocalDateTime.now();
                    order.setReceiveOrderTime(now);
                    //地图中来
                    order.setReceiveOrderCarLongitude(longitude);
                    order.setReceiveOrderCarLatitude(latitude);
                    //从司机和车辆来
                    order.setLicenseId(availableDriverData.getLicenseId());
                    order.setVehicleNo(availableDriverData.getVehicleNo());
//                    order.setVehicleType(vehicleTypeFromCar);//改成从乘客中来
                    order.setOrderStatus(OrderConstants.DRIVER_RECEIVE_ORDER);

                    order.setGmtModified(now);
                    orderMapper.updateById(order);

                    //通知司机
                    JSONObject driverContent = new JSONObject();
                    driverContent.put("orderId", order.getId());
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
                    passengerContent.put("orderId", order.getId());
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
        ResponseResult<Boolean> booleanResponseResult = servicePriceClient.ifExists(cityCode, vehicleType);
        return booleanResponseResult.getData();
    }

    /**
     * 从fareType得到cityCode和vehicleType
     * @param fareType
     * @return
     */
    private HashMap<String, String> getCityCodeVehicleType(String fareType){
        int $ = fareType.indexOf("$");
        String cityCode = fareType.substring(0, $);
        String vehicleType = fareType.substring($ + 1);
        HashMap<String, String> map = new HashMap<>();
        map.put("cityCode", cityCode);
        map.put("vehicleType", vehicleType);
        return map;
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
            if (i >= 20 ){
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
        //上面是填写下车地点和时间，下面是形成距离和时长
        ResponseResult<Car> carById = serviceDriverUserClient.getCarById(order.getCarId());
        String tid = carById.getData().getTid();
        long starttime = order.getPickUpPassengerTime().atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        long endtime = ZonedDateTime.now(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
        ResponseResult<TrsearchResponse> trsearch = serviceMapClient.trsearch(tid, starttime, endtime);
        TrsearchResponse trsearchData = trsearch.getData();
        Long distanceMile = trsearchData.getDistanceMile();
        Long timeMin = trsearchData.getTimeMin();
        order.setDriveMile(distanceMile);
        order.setDriveTime(timeMin);
        //获取价格
        String fareType = order.getFareType();
        //getFareType可以，getVehicleType和Address也行
        ResponseResult<Double> priceResponseResult = servicePriceClient.calculatePrice(distanceMile.intValue(), timeMin.intValue(), fareType);
        Double price = priceResponseResult.getData();
        order.setPrice(price);

        orderMapper.updateById(order);
        return ResponseResult.success("到达目的地");
    }

    /**
     * 司机推送订单付款信息
     * @param orderRequest
     * @return
     */
    public ResponseResult pushPayInfo(OrderRequest orderRequest) {

        Order order = new Order();
        order.setId(orderRequest.getOrderId());
        order.setOrderStatus(OrderConstants.TO_START_PAY);
        orderMapper.updateById(order);

        return ResponseResult.success("司机发起收款");
    }
    /**
     * 支付完成
     * @param orderRequest
     * @return
     */
    public ResponseResult pay(OrderRequest orderRequest) {
        Long orderId = orderRequest.getOrderId();
        Order order = orderMapper.selectById(orderId);
        order.setOrderStatus(OrderConstants.SUCCESS_PAY);
        orderMapper.updateById(order);
        return ResponseResult.success("支付完成");
    }

    /**
     * 订单取消
     * @param orderId
     * @param identity
     * @return
     */
    public ResponseResult cancel(Long orderId, String identity) {
        //查询订单当前状态
        Order order = orderMapper.selectById(orderId);
        Integer orderStatus = order.getOrderStatus();
        LocalDateTime cancelTime = LocalDateTime.now();
        Integer cancelOperator = null;
        Integer cancelTypeCode = null;
        //更新订单的取消状态
        int cancelType = 1;
        //乘客取消
        if (identity.trim().equals(IdentityConstants.PASSENGER_IDENTITY)){
            switch (orderStatus){
                //订单发起1
                case OrderConstants.ORDER_START -> cancelTypeCode = OrderConstants.CANCEL_PASSENGER_BEFORE;
                //司机接单2
                case OrderConstants.DRIVER_RECEIVE_ORDER -> {
                    LocalDateTime receiveOrderTime = order.getReceiveOrderTime();
                    long between = ChronoUnit.MINUTES.between(receiveOrderTime, cancelTime);
                    if (between > 1){
                        cancelTypeCode = OrderConstants.CANCEL_PASSENGER_ILLEGAL;
                    }else {
                        cancelTypeCode = OrderConstants.CANCEL_PASSENGER_BEFORE;
                    }
                }
                //司机去接乘客3
                case OrderConstants.DRIVER_TO_PICK_UP_PASSENGER -> cancelTypeCode = OrderConstants.CANCEL_PASSENGER_ILLEGAL;
                //司机抵达上车点4
                case OrderConstants.DRIVER_ARRIVED_DEPARTURE -> cancelTypeCode = OrderConstants.CANCEL_PASSENGER_ILLEGAL;
                default -> {
                    log.info("乘客取消失败");
                    cancelType = 0;
                }
            }
        }
        //司机取消
        if (identity.trim().equals(IdentityConstants.DRIVER_IDENTITY)){
            switch (orderStatus){
                case OrderConstants.DRIVER_RECEIVE_ORDER,
                     OrderConstants.DRIVER_TO_PICK_UP_PASSENGER,
                     OrderConstants.DRIVER_ARRIVED_DEPARTURE -> {
                        LocalDateTime receiveOrderTime = order.getReceiveOrderTime();
                        long between = ChronoUnit.MINUTES.between(receiveOrderTime, cancelTime);
                        if (between > 1){
                            cancelTypeCode = OrderConstants.CANCEL_DRIVER_ILLEGAL;
                        }else {
                            cancelTypeCode = OrderConstants.CANCEL_DRIVER_BEFORE;
                        }
                    }
                default -> {
                    log.info("司机取消失败");
                    cancelType = 0;
                }
            }
        }

        if (cancelType == 0){
            return ResponseResult.fail(CommonStatusEnum.ORDER_CANCEL_ERROR.getCode(), CommonStatusEnum.ORDER_CANCEL_ERROR.getValue());
        }
        order.setCancelTypeCode(cancelTypeCode);
        order.setCancelTime(cancelTime);
        order.setCancelOperator(Integer.parseInt(identity));
        order.setOrderStatus(OrderConstants.ORDER_CANCEL);

        orderMapper.updateById(order);

        return ResponseResult.success("订单取消类型为"+cancelTypeCode);
    }

    /**
     * 查询用户正在进行中的订单
     * @param userId
     * @param identity
     * @return
     */
    public ResponseResult getOrder(Long userId, String identity) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        if (identity.trim().equals(IdentityConstants.PASSENGER_IDENTITY)){
            queryWrapper.eq("passenger_id", userId);
            queryWrapper.and(wrapper -> wrapper.eq("order_status", OrderConstants.ORDER_START)
                    .or().eq("order_status", OrderConstants.DRIVER_RECEIVE_ORDER)
                    .or().eq("order_status", OrderConstants.DRIVER_TO_PICK_UP_PASSENGER)
                    .or().eq("order_status", OrderConstants.DRIVER_ARRIVED_DEPARTURE)
                    .or().eq("order_status", OrderConstants.PICK_UP_PASSENGER)
                    .or().eq("order_status", OrderConstants.PASSENGER_GETOFF)
                    .or().eq("order_status", OrderConstants.TO_START_PAY));
        } else if (identity.trim().equals(IdentityConstants.DRIVER_IDENTITY)) {
            queryWrapper.eq("driver_id", userId);
            queryWrapper.and(wrapper -> wrapper.eq("order_status", OrderConstants.DRIVER_RECEIVE_ORDER)
                    .or().eq("order_status", OrderConstants.DRIVER_TO_PICK_UP_PASSENGER)
                    .or().eq("order_status", OrderConstants.DRIVER_ARRIVED_DEPARTURE)
                    .or().eq("order_status", OrderConstants.PICK_UP_PASSENGER)
                    .or().eq("order_status", OrderConstants.PASSENGER_GETOFF));
        }
        Order order = orderMapper.selectOne(queryWrapper);
        if (order == null){
            return ResponseResult.success("没有正在进行中的订单");
        }else {
            return ResponseResult.success("正在进行中的订单ID:" + order.getId());
        }
    }
}
