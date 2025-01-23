package com.dsa.apipassenger.service;

import com.dsa.apipassenger.remote.ServicePassengerUserClient;
import com.dsa.apipassenger.remote.ServiceVerificationCodeClient;
import com.dsa.internalcommon.constant.CommonStatusEnum;
import com.dsa.internalcommon.constant.IdentityContant;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.VerificationCodeDTO;
import com.dsa.internalcommon.responese.NumberCodeResponse;
import com.dsa.internalcommon.responese.TokenResponse;
import com.dsa.internalcommon.util.JwtUtils;
import io.micrometer.common.util.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

    private String verificationCodePrefix = "passenger-verification-code-";

    @Autowired
    ServiceVerificationCodeClient serviceVerificationCodeClient;
    @Autowired
    private ServicePassengerUserClient servicePassengerUserClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    public ResponseResult generatorCode(String passengerPhone){

        ResponseResult<NumberCodeResponse> numberCodeResponse = serviceVerificationCodeClient.getNumberCode(5);
        int numberCode = numberCodeResponse.getData().getNumberCode();

//      存入redis
        String key = generatorKeyByPhone(passengerPhone);
        stringRedisTemplate.opsForValue().set(key, numberCode + "", 2, TimeUnit.MINUTES);

        return ResponseResult.success();
    }

    public ResponseResult checkCode(String passengerPhone, String verificationCode){
        System.out.println("去redis读对应手机号的验证码");
        String key = generatorKeyByPhone(passengerPhone);
        String codeRedis = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(codeRedis)){
            System.out.println("用户未获取验证码");
            return ResponseResult.fail(CommonStatusEnum.VERIFY_CODE_ERROR.getCode(), CommonStatusEnum.VERIFY_CODE_ERROR.getValue());
        }
        if (!codeRedis.equals(verificationCode)){
            System.out.println("验证码错误");
            return ResponseResult.fail(CommonStatusEnum.VERIFY_CODE_ERROR.getCode(), CommonStatusEnum.VERIFY_CODE_ERROR.getValue());
        }
        System.out.println("判断是否已存在用户");
        VerificationCodeDTO verificationCodeDTO = new VerificationCodeDTO(passengerPhone, verificationCode);
        ResponseResult responseResult = servicePassengerUserClient.loginOrRegister(verificationCodeDTO);
        System.out.println("发令牌");
        String token = JwtUtils.generatorToken(passengerPhone, IdentityContant.PASSENGER_IDENTITY);//这个1表示乘客，应该用常量
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(token);

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
