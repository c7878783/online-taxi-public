package com.dsa.serviceorder.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dsa.internalcommon.constant.CommonStatusEnum;
import com.dsa.internalcommon.constant.OrderConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Order;
import com.dsa.internalcommon.pojo.PriceRule;
import com.dsa.internalcommon.request.OrderRequest;
import com.dsa.internalcommon.util.RedisPrefixUtils;
import com.dsa.serviceorder.mapper.OrderMapper;
import com.dsa.serviceorder.remote.ServiceClient;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class OrderService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    ServiceClient serviceClient;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    public ResponseResult add(OrderRequest orderRequest){
        LocalDateTime now = LocalDateTime.now();
        //下单的城市和车型是否支持业务
        if (!ifPriceRuleExists(orderRequest.getFareType())){
            return ResponseResult.fail(CommonStatusEnum.CITY_NOT_SETVICE.getCode(), CommonStatusEnum.CITY_NOT_SETVICE.getValue());
        }
        //判断下单设备是否是黑名单设备
        if (isBlackDevice(orderRequest.getDeviceCode())){
            return ResponseResult.fail(CommonStatusEnum.DEVICE_IS_BLACK.getCode(), CommonStatusEnum.DEVICE_IS_BLACK.getValue());
        }
        //判断是否有正在进行中的订单
        Long validOrderNum = ifOrderGoingOn(orderRequest.getPassengerId());
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
        order.setGmtCreate(now);
        order.setGmtModified(now);

        orderMapper.insert(order);
        return ResponseResult.success("");
    }

    private boolean ifPriceRuleExists(String fareType){
        int $ = fareType.indexOf("$");
        String cityCode = fareType.substring(0, $);
        String vehicleType = fareType.substring($ + 1);
        ResponseResult<Boolean> booleanResponseResult = serviceClient.ifExists(cityCode, vehicleType);
        return booleanResponseResult.getData();
    }

    private Long ifOrderGoingOn(Long passengerId){
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
    }}
