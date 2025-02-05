package com.dsa.testalipay.service;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Order;
import com.dsa.internalcommon.request.OrderRequest;
import com.dsa.testalipay.remote.ServiceOrderClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ResourceBundle;

@Service
public class AlipayService {

    @Autowired
    ServiceOrderClient serviceOrderClient;

    public ResponseResult pay(Long orderId){

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderId(orderId);

        return serviceOrderClient.pay(orderRequest);
    }
}
