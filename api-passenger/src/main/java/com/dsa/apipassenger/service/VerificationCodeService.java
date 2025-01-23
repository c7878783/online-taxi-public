package com.dsa.apipassenger.service;

import com.dsa.apipassenger.remote.ServicePassengerUserClient;
import com.dsa.apipassenger.remote.ServiceVerificationCodeClient;
import com.dsa.internalcommon.constant.CommonStatusEnum;
import com.dsa.internalcommon.constant.IdentityConstants;
import com.dsa.internalcommon.constant.TokenConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.VerificationCodeDTO;
import com.dsa.internalcommon.responese.NumberCodeResponse;
import com.dsa.internalcommon.responese.TokenResponse;
import com.dsa.internalcommon.util.JwtUtils;
import com.dsa.internalcommon.util.RedisPrefixUtils;
import io.micrometer.common.util.StringUtils;
import org.apache.el.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class VerificationCodeService {

//    private String verificationCodePrefix = "passenger-verification-code-";
//    private String tokenPrefix = "token-";

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
        String key = RedisPrefixUtils.generatorKeyByPhone(passengerPhone);
        stringRedisTemplate.opsForValue().set(key, numberCode + "", 2, TimeUnit.MINUTES);

        return ResponseResult.success();
    }

    public ResponseResult checkCode(String passengerPhone, String verificationCode){
        System.out.println("去redis读对应手机号的验证码");
        String key = RedisPrefixUtils.generatorKeyByPhone(passengerPhone);
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
        String accessToken = JwtUtils.generatorToken(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);//这个1表示乘客，应该用常量
        String refreshToken = JwtUtils.generatorToken(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);//这个1表示乘客，应该用常量
        //存token到redis
        String accessTokenKey = RedisPrefixUtils.generateTokenKey(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);
        String refreshTokenKey = RedisPrefixUtils.generateTokenKey(passengerPhone, IdentityConstants.PASSENGER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);

        stringRedisTemplate.opsForValue().set(accessTokenKey, accessToken, 10, TimeUnit.DAYS);
        stringRedisTemplate.opsForValue().set(refreshTokenKey, refreshToken, 11, TimeUnit.DAYS);//refreshToken要晚过期一会

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);

        return ResponseResult.success(tokenResponse);
    }
//    迁移到common.utils当中去了
//    /**
//     * @param passengerPhone 手机号码
//     * @param identity 身份(司机或乘客)
//     * @return 根据手机号码和身份生成的，用在redis中的token key
//     */
//    private String generateTokenKey(String passengerPhone, String identity){
//        return tokenPrefix + passengerPhone + identity;
//    }
//    /**
//     * 当有复制代码的想法时，就应该考虑抽取方法.根据手机号得到redis key
//     * @param passengerPhone 手机号码
//     * @return 根据手机号码生成的，用于在redis中存取验证码的key
//     */
//    private String generatorKeyByPhone(String passengerPhone){
//        return verificationCodePrefix + passengerPhone;
//    }
}
