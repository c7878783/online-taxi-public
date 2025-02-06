package com.dsa.apipassenger.service;

import com.dsa.apipassenger.remote.ServiceOrderClient;
import com.dsa.internalcommon.constant.IdentityConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    ServiceOrderClient serviceOrderClient;

    public ResponseResult add(OrderRequest orderRequest){
        return serviceOrderClient.add(orderRequest);
    }

    public ResponseResult cancel(Long orderId) {
        return serviceOrderClient.cancel(orderId, IdentityConstants.PASSENGER_IDENTITY);
    }

    public ResponseResult getOrder(Long passengerId) {

        String passengerIdentity = IdentityConstants.PASSENGER_IDENTITY;

        return serviceOrderClient.getOrder(passengerId, passengerIdentity);
    }
}
