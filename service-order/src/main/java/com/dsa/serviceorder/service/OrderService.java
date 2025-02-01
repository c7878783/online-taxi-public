package com.dsa.serviceorder.service;

import com.dsa.internalcommon.constant.OrderConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Order;
import com.dsa.internalcommon.request.OrderRequest;
import com.dsa.serviceorder.mapper.OrderMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    OrderMapper orderMapper;

    public ResponseResult add(OrderRequest orderRequest){
        LocalDateTime now = LocalDateTime.now();

        Order order = new Order();

        BeanUtils.copyProperties(orderRequest, order);
        order.setOrderStatus(OrderConstants.ORDER_START);
        order.setGmtCreate(now);
        order.setGmtModified(now);


        orderMapper.insert(order);
        return ResponseResult.success("");
    }

}
