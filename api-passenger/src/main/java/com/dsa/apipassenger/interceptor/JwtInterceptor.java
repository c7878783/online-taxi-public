package com.dsa.apipassenger.interceptor;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.util.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.security.SignatureException;

public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        boolean result = true;
        String resultString = "";

        String token = request.getHeader("Authorization");//这就是token
        try {
            JwtUtils.paresToken(token);
        }catch (SignatureVerificationException e){
            resultString = "toekn sign error";
            result = false;
        }catch (TokenExpiredException e){
            resultString = "token expired";
            result = false;
        }catch (AlgorithmMismatchException e){
            resultString = "algorithm mismatch";
            result = false;
        }catch (Exception e){
            resultString = "token invalid";
            result = false;
        }
        if (!result){
            PrintWriter writer = response.getWriter();
            writer.print(JSONObject.fromObject(ResponseResult.fail(resultString)).toString());
        }
        return result;
    }
}
