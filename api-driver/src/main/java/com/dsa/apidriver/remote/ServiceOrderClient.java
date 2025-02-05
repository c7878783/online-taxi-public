package com.dsa.apidriver.remote;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.OrderRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("service-order")
public interface ServiceOrderClient {

    @PostMapping("/order/to-pick-up-passenger")
    public ResponseResult toPickUp(@RequestBody OrderRequest orderRequest);


    @PostMapping("/order/arrived-departure")
    public ResponseResult arrivedDeparture(@RequestBody OrderRequest orderRequest);


    @PostMapping("/order/pick-up-passenger")
    public ResponseResult pickUp(@RequestBody OrderRequest orderRequest);


    @PostMapping("/order/passenger-getoff")
    public ResponseResult passengerGet0ff(@RequestBody OrderRequest orderRequest);

    @PostMapping("/order/push-pay-info")
    public ResponseResult pushPayInfo(@RequestBody OrderRequest orderRequest);

    @PostMapping("/order/cancel")
    public ResponseResult cancel(@RequestParam Long orderId, @RequestParam String identity);

}
