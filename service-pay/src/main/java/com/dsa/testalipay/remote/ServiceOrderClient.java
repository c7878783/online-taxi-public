package com.dsa.testalipay.remote;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.OrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("service-order")
public interface ServiceOrderClient {

    @PostMapping("/order/pay")
    public ResponseResult pay(@RequestBody OrderRequest orderRequest);

}
