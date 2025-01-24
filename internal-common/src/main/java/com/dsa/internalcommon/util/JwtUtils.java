package com.dsa.internalcommon.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.dsa.internalcommon.constant.TokenConstants;
import com.dsa.internalcommon.dto.TokenResult;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {
    //sign
    private static final String SIGN = "CPFdsa!@#$%";

    private static final String JWT_KEY_PHONE = "passengerPhone";
    private static final String JWT_KEY_IDENTITY = "identity";
    private static final String JWT_TOKEN_TYPE = "tokenType";
    private static final String JWT_TOKEN_TIME = "time";

    public static String generatorToken(String passengerPhone, String identity, String tokenType){
        HashMap<String, String> map = new HashMap<>();
        map.put(JWT_KEY_PHONE, passengerPhone);
        map.put(JWT_KEY_IDENTITY, identity);
        map.put(JWT_TOKEN_TYPE, tokenType);
        Calendar calendar = Calendar.getInstance();//用于expire的calendar实例，后来为了得到时间戳又拿了回来
        Date date = calendar.getTime();//时间戳，防止token一直相同
        map.put(JWT_TOKEN_TIME, date.toString());
//        calendar.add(Calendar.DATE,1);//在api-passenger里已经实现了redis中的过期设置，所以不再需要
//        Date date = calendar.getTime();

        JWTCreator.Builder builder = JWT.create();

        map.forEach(
                (k, v) -> {
                builder.withClaim(k,v);
            }
        );

//        builder.withExpiresAt(date);

        String sign = builder.sign(Algorithm.HMAC256(SIGN));

        return sign;
    }

    public static TokenResult  paresToken(String token){
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
        String phone = verify.getClaim(JWT_KEY_PHONE).asString();//asString和toString缺几个引号，注意
        String identity = verify.getClaim(JWT_KEY_IDENTITY).asString();
        TokenResult tokenResult = new TokenResult();
        tokenResult.setPhone(phone);
        tokenResult.setIdentity(identity);

        return tokenResult;
    }

    /**
     * 校验token，主要判断token是否异常，判断token编码规则，是否和redis中的token相同在return后进行
     * @param token 传入的token
     * @return 校验结果
     */
    public static TokenResult checkToken(String token){
        TokenResult tokenResult = null;
        try {
            tokenResult = JwtUtils.paresToken(token);
        }catch (Exception e){

        }
        return tokenResult;
    }

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
//        map.put("name","bababa");
//        map.put("gender","1");

        String s = generatorToken("110", "1", TokenConstants.ACCESS_TOKEN_TYPE);
        System.out.println("生成的toekn是" + s);
        System.out.println("解析后的值" + paresToken(s));
    }
}
