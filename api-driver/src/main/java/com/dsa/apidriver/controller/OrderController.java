package com.dsa.apidriver.controller;

import com.dsa.apidriver.service.OrderService;
import com.dsa.internalcommon.constant.CommonStatusEnum;
import com.dsa.internalcommon.constant.IdentityConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.dto.TokenResult;
import com.dsa.internalcommon.pojo.Order;
import com.dsa.internalcommon.request.OrderRequest;
import com.dsa.internalcommon.util.JwtUtils;
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

    @GetMapping("/current")
    public ResponseResult<Order> currentOrder(HttpServletRequest httpServletRequest){
        String authorization = httpServletRequest.getHeader("Authorization");
        TokenResult tokenResult = JwtUtils.paresToken(authorization);
        String identity = tokenResult.getIdentity();
        if (!identity.equals(IdentityConstants.DRIVER_IDENTITY)){
            return ResponseResult.fail(CommonStatusEnum.TOKEN_ERROR.getCode(),CommonStatusEnum.TOKEN_ERROR.getValue());
        }
        String phone = tokenResult.getPhone();

        return orderService.currentOrder(phone,IdentityConstants.DRIVER_IDENTITY);
    }

    @GetMapping("/detail")
    public ResponseResult<Order> detail(Long orderId){
        return orderService.detail(orderId);
    }
}
