package com.dsa.apidriver.service;

import com.dsa.apidriver.remote.ServiceOrderClient;
import com.dsa.apidriver.remote.ServiceSsePushClient;
import com.dsa.internalcommon.constant.IdentityConstants;
import com.dsa.internalcommon.constant.OrderConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Order;
import com.dsa.internalcommon.request.OrderRequest;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayService {

    @Autowired
    ServiceSsePushClient serviceSsePushClient;
    @Autowired
    ServiceOrderClient serviceOrderClient;

    public ResponseResult pushPayInfo(Long orderId, String price, Long passengerId){
        //封装消息
        JSONObject message = new JSONObject();
        message.put("price", price);
        //推送消息
        serviceSsePushClient.push(passengerId, IdentityConstants.PASSENGER_IDENTITY, message.toString());
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderId(orderId);

        return serviceOrderClient.pushPayInfo(orderRequest);

    }
}
