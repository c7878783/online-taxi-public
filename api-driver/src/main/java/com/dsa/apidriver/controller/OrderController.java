package com.dsa.apidriver.controller;

import com.dsa.apidriver.service.OrderService;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.OrderRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    /**
     * 司机出发接乘客（订单状态3）
     * @param orderRequest
     * @return
     */
    @PostMapping("/to-pick-up-passenger")
    public ResponseResult toPickUp(@RequestBody OrderRequest orderRequest){
        return orderService.toPickUp(orderRequest);
    }

    /**
     * 抵达上车点只需要知道时间，一般网约车在抵达上车点后等一会才能接到乘客（状态4）
     * @param orderRequest
     * @return
     */
    @PostMapping("/arrived-departure")
    public ResponseResult arrivedDeparture(@RequestBody OrderRequest orderRequest){
        return orderService.arrivedDeparture(orderRequest);
    }

    /**
     * 乘客上车，形成开始（5）
     * @param orderRequest
     * @return
     */
    @PostMapping("/pick-up-passenger")
    public ResponseResult pickUp(@RequestBody OrderRequest orderRequest){
        return orderService.pickUp(orderRequest);
    }

    /**
     * 到达目的地（6）
     * @param orderRequest
     * @return
     */
    @PostMapping("/passenger-getoff")
    public ResponseResult passengerGet0ff(@RequestBody OrderRequest orderRequest){
        return orderService.passengerGetoff(orderRequest);
    }

    @PostMapping("/cancel")
    public ResponseResult cancel(@RequestParam Long orderId){
        return orderService.cancel(orderId);
    }

    @PostMapping("/get-order")
    public ResponseResult getOrder(@RequestParam Long driverId){
        return orderService.getOrder(driverId);
    }
}
