package com.dsa.serviceorder.controller;

import com.dsa.internalcommon.constant.HeaderParamConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.OrderRequest;
import com.dsa.serviceorder.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    /**
     * 乘客下订单并开始实时寻找司机（订单状态1~2）
     * @param orderRequest
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/add")
    public ResponseResult add(@RequestBody OrderRequest orderRequest, HttpServletRequest httpServletRequest){
            //测试通过，通过header获取设备码deviceCode
//        String deviceCode = httpServletRequest.getHeader(HeaderParamConstants.DEVICE_CODE);
//        orderRequest.setDeviceCode(deviceCode);
        System.out.println(orderRequest);
        return orderService.add(orderRequest);
    }

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


}
