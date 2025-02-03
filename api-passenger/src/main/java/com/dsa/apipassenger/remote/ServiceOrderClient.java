package com.dsa.apipassenger.remote;

import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Order;
import com.dsa.internalcommon.request.OrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("service-order")
public interface ServiceOrderClient {

    @RequestMapping (method = RequestMethod.POST, value = "/order/add")
    public ResponseResult add(@RequestBody OrderRequest orderRequest);

    @GetMapping("/test-real-time-order")
    public String dispatchRealaTimeOrder(@RequestParam long orderId);

}
