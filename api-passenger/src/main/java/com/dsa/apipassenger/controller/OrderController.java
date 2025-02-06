package com.dsa.apipassenger.controller;

import com.dsa.apipassenger.service.OrderService;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/add")
    public ResponseResult add(@RequestBody OrderRequest orderRequest){

        return orderService.add(orderRequest);
    }

    @PostMapping("cancel")
    public ResponseResult cancel(@RequestParam Long orderId){
        return orderService.cancel(orderId);
    }

    @PostMapping("/get-order")
    public ResponseResult getOrder(@RequestParam Long passengerId){
        return orderService.getOrder(passengerId);
    }
}
