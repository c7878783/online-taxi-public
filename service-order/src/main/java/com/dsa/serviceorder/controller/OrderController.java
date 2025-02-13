package com.dsa.serviceorder.controller;

import com.dsa.internalcommon.constant.HeaderParamConstants;
import com.dsa.internalcommon.constant.OrderConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.pojo.Order;
import com.dsa.internalcommon.request.OrderRequest;
import com.dsa.serviceorder.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 司机发起收款(7)
     * @param orderRequest
     * @return
     */
    @PostMapping("/push-pay-info")
    public ResponseResult pushPayInfo(@RequestBody OrderRequest orderRequest){
        return orderService.pushPayInfo(orderRequest);
    }

    /**
     * 支付完成(8)
     * @param orderRequest
     * @return
     */
    @PostMapping("/pay")
    public ResponseResult pay(@RequestBody OrderRequest orderRequest){
        return orderService.pay(orderRequest);
    }

    /**
     * 订单取消(9)
     * @param orderId
     * @param identity
     * @return
     */
    @PostMapping("/cancel")
    public ResponseResult cancel(@RequestParam Long orderId, @RequestParam String identity){
        return orderService.cancel(orderId, identity);
    }
    /**
     * 查询用户(司机/乘客)正在进行中的订单
     * @param userId
     * @param identity
     * @return
     */
    @PostMapping("/get-order")
    public ResponseResult getOrder(@RequestParam Long userId, @RequestParam String identity){
        return orderService.getOrder(userId, identity);
    }
    /**
     * 查询用户(司机/乘客)正在进行中的订单
     * @param phone
     * @param identity
     * @return
     */
    @GetMapping("/current")
    public ResponseResult current(String phone , String identity){
        return orderService.current(phone , identity);
    }

    /**
     * 订单详情
     * @param orderId
     * @return
     */
    @GetMapping("/detail")
    public ResponseResult<Order> detail(Long orderId){
        return orderService.detail(orderId);
    }

}
