package com.dsa.apipassenger.service;

import com.dsa.apipassenger.remote.ServiceOrderClient;
import com.dsa.apipassenger.remote.ServicePassengerUserClient;
import com.dsa.internalcommon.constant.IdentityConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Order;
import com.dsa.internalcommon.pojo.PassengerUser;
import com.dsa.internalcommon.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    @Autowired
    ServiceOrderClient serviceOrderClient;

//    @Autowired
//    ServicePassengerUserClient servicePassengerUserClient;

    public ResponseResult add(OrderRequest orderRequest){
//        ResponseResult<PassengerUser> userResponseResult = servicePassengerUserClient.getUser(orderRequest.getPassengerPhone());
//        Long userId = userResponseResult.getData().getId();
//        orderRequest.setPassengerId(userId);
        return serviceOrderClient.add(orderRequest);
    }

    public ResponseResult cancel(Long orderId) {
        return serviceOrderClient.cancel(orderId, IdentityConstants.PASSENGER_IDENTITY);
    }

    public ResponseResult<Order> currentOrder(String phone , String identity){
        return serviceOrderClient.current(phone,identity);
    }

    public ResponseResult<Order> detail(Long orderId){
        return serviceOrderClient.detail(orderId);
    }
}
