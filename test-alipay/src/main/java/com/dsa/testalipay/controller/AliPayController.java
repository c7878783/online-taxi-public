package com.dsa.testalipay.controller;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.payment.page.models.AlipayTradePagePayResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/alipay")
@ResponseBody
public class AliPayController {
    @GetMapping("/pay")
    public String pay(@RequestParam String subject, @RequestParam String outTradeNo, @RequestParam String totalAmount){
        AlipayTradePagePayResponse response;
        try {
            response = Factory.Payment.Page().pay(subject, outTradeNo, totalAmount, "");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        return response.getBody();
    }
    @PostMapping("/notify")
    public String notifi(HttpServletRequest request) throws Exception {
        String tradeStatus = request.getParameter("trade_status");
        if (tradeStatus.trim().equals("TRADE_SUCCESS")){
            Map<String, String> param = new HashMap<>();
            Map<String, String[]> parameterMap = request.getParameterMap();
            for (String name : parameterMap.keySet()) {
                param.put(name, request.getParameter(name));
            }

            if (Factory.Payment.Common().verifyNotify(param)){
                System.out.println("通过支付宝验证");
                for (String name : param.keySet()) {
                    System.out.println("收到的参数:" + name + "," + param.get(name));
                }
            }else {
                System.out.println("支付宝验证不通过");
            }
        }

        return "success";
    }
}
