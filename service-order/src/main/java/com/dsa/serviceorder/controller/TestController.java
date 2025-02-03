package com.dsa.serviceorder.controller;

import com.dsa.internalcommon.pojo.Order;
import com.dsa.serviceorder.mapper.OrderMapper;
import com.dsa.serviceorder.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderService orderService;
    @Value("${server.port}")
    String port;

    @GetMapping("/test-real-time-order")
    public String dispatchRealaTimeOrder(@RequestParam long orderId){

//        System.out.println(orderId + "----" + port);
        Order order = orderMapper.selectById(orderId);
        orderService.dispatchRealTimeOrder(order);
        return "test-real-time-order   success";
    }

}
