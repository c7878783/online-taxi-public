package com.dsa.apidriver.service;

import com.dsa.apidriver.controller.OrderController;
import com.dsa.apidriver.remote.ServiceOrderClient;
import com.dsa.internalcommon.constant.IdentityConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Order;
import com.dsa.internalcommon.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    ServiceOrderClient serviceOrderClient;

    public ResponseResult toPickUp(OrderRequest orderRequest) {
        return serviceOrderClient.toPickUp(orderRequest);
    }

    public ResponseResult arrivedDeparture(OrderRequest orderRequest) {
        return serviceOrderClient.arrivedDeparture(orderRequest);
    }

    public ResponseResult pickUp(OrderRequest orderRequest) {
        return serviceOrderClient.pickUp(orderRequest);
    }

    public ResponseResult passengerGetoff(OrderRequest orderRequest) {
        return serviceOrderClient.passengerGet0ff(orderRequest);
    }

    public ResponseResult cancel(Long orderId) {
        return serviceOrderClient.cancel(orderId, IdentityConstants.DRIVER_IDENTITY);
    }

    public ResponseResult getOrder(Long driverId) {

        String driverIdentity = IdentityConstants.DRIVER_IDENTITY;

        return serviceOrderClient.getOrder(driverId, driverIdentity);
    }

    public ResponseResult<Order> currentOrder(String phone , String identity){
        return serviceOrderClient.current(phone,identity);
    }

    public ResponseResult<Order> detail(Long orderId){
        return serviceOrderClient.detail(orderId);
    }
}
