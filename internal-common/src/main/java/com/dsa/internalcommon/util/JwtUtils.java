package com.dsa.internalcommon.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
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

    public static String generatorToken(String passengerPhone, String identity){
        HashMap<String, String> map = new HashMap<>();
        map.put(JWT_KEY_PHONE, passengerPhone);
        map.put(JWT_KEY_IDENTITY, identity);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        Date date = calendar.getTime();

        JWTCreator.Builder builder = JWT.create();

        map.forEach(
                (k, v) -> {
                builder.withClaim(k,v);
            }
        );

        builder.withExpiresAt(date);

        String sign = builder.sign(Algorithm.HMAC256(SIGN));

        return sign;
    }

    public static TokenResult  paresToken(String token){
        DecodedJWT verify = JWT.require(Algorithm.HMAC256(SIGN)).build().verify(token);
        String phone = verify.getClaim(JWT_KEY_PHONE).toString();
        String identity = verify.getClaim(JWT_KEY_IDENTITY).toString();
        TokenResult tokenResult = new TokenResult();
        tokenResult.setPhone(phone);
        tokenResult.setIdentity(identity);

        return tokenResult;
    }

    public static void main(String[] args) {
        Map<String, String> map = new HashMap<>();
//        map.put("name","bababa");
//        map.put("gender","1");

        String s = generatorToken("110", "1");
        System.out.println("生成的toekn是" + s);
        System.out.println("解析后的值" + paresToken(s));
    }
}
