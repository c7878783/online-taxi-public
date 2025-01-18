package com.dsa.apipassenger.service;

import com.dsa.apipassenger.remote.ServiceVerificationCodeClient;
import com.dsa.internalcommon.constant.CommonStatusEnum;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.responese.NumberCodeResponse;
import com.dsa.internalcommon.responese.TokenResponse;
import io.micrometer.common.util.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    @Autowired
    ServiceVerificationCodeClient serviceVerificationCodeClient;

    private String verificationCodePrefix = "passenger-verification-code-";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public ResponseResult generatorCode(String passengerPhone){

        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerificationCodeClient.getNumberCode(5);
        int numberCode = numberCodeResponse.getData().getNumberCode();

//      存入redis
        String key = generatorKeyByPhone(passengerPhone);
        stringRedisTemplate.opsForValue().set(key, numberCode + "", 1, TimeUnit.MINUTES);

        return ResponseResult.success();
    }

    public ResponseResult checkCode(String passengerPhone, String verificationCode){
        System.out.println("去redis读对应手机号的验证码");
        String key = generatorKeyByPhone(passengerPhone);
        String codeRedis = stringRedisTemplate.opsForValue().get(key);
        System.out.println("redis中读出来的数字" + codeRedis);
        System.out.println("比对");
        if (StringUtils.isBlank(codeRedis)){
            return ResponseResult.fail(CommonStatusEnum.VERIFY_CODE_ERROR.getCode(), CommonStatusEnum.VERIFY_CODE_ERROR.getValue());
        }
        if (!codeRedis.equals(verificationCode)){
            return ResponseResult.fail(CommonStatusEnum.VERIFY_CODE_ERROR.getCode(), CommonStatusEnum.VERIFY_CODE_ERROR.getValue());
        }
        System.out.println("判断是否已存在用户");

        System.out.println("发令牌");

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken("toke n");

        return ResponseResult.success(tokenResponse);
    }

    /**
     * 当有复制代码的想法时，就应该考虑抽取方法
     * @param passengerPhone
     * @return
     */
    private String generatorKeyByPhone(String passengerPhone){
        return verificationCodePrefix + passengerPhone;
    }
}
