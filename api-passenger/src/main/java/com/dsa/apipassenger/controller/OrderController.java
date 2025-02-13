package com.dsa.apipassenger.controller;

import com.dsa.apipassenger.service.OrderService;
import com.dsa.apipassenger.service.UserService;
import com.dsa.internalcommon.constant.CommonStatusEnum;
import com.dsa.internalcommon.constant.IdentityConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.dto.TokenResult;
import com.dsa.internalcommon.pojo.Order;
import com.dsa.internalcommon.pojo.PassengerUser;
import com.dsa.internalcommon.request.OrderRequest;
import com.dsa.internalcommon.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;
    @Autowired
    UserService userService;

    @PostMapping("/add")
    public ResponseResult add(@RequestBody OrderRequest orderRequest, HttpServletRequest request){

        String accessToken = request.getHeader("Authorization");
//        TokenResult tokenResult = JwtUtils.checkToken(token);
//        orderRequest.setPassengerPhone(tokenResult.getPhone());
        ResponseResult<PassengerUser> userByAccessToken = userService.getUserByAccessToken(accessToken);
        PassengerUser passengerUser = userByAccessToken.getData();
        orderRequest.setPassengerId(passengerUser.getId());
        orderRequest.setPassengerPhone(passengerUser.getPassengerPhone());
        return orderService.add(orderRequest);
    }

    @PostMapping("cancel")
    public ResponseResult cancel(@RequestParam Long orderId){
        return orderService.cancel(orderId);
    }

    @GetMapping("/current")
    public ResponseResult<Order> currentOrder(HttpServletRequest httpServletRequest){
        String authorization = httpServletRequest.getHeader("Authorization");
        TokenResult tokenResult = JwtUtils.paresToken(authorization);
        String identity = tokenResult.getIdentity();
        if (!identity.equals(IdentityConstants.PASSENGER_IDENTITY)){
            return ResponseResult.fail(CommonStatusEnum.TOKEN_ERROR.getCode(),CommonStatusEnum.TOKEN_ERROR.getValue());
        }
        String phone = tokenResult.getPhone();

        return orderService.currentOrder(phone,IdentityConstants.PASSENGER_IDENTITY);
    }

    @GetMapping("/detail")
    public ResponseResult<Order> detail(Long orderId){
        return orderService.detail(orderId);
    }
}
