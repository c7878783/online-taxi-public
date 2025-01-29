package com.dsa.apidriver.service;

import com.dsa.apidriver.remote.ServiceDriverUserClient;
import com.dsa.apidriver.remote.ServiceVerificationCodeClient;
import com.dsa.internalcommon.constant.CommonStatusEnum;
import com.dsa.internalcommon.constant.DriverCarConstants;
import com.dsa.internalcommon.constant.IdentityConstants;
import com.dsa.internalcommon.constant.TokenConstants;
import com.dsa.internalcommon.dto.ResponseResult;
import com.dsa.internalcommon.request.VerificationCodeDTO;
import com.dsa.internalcommon.responese.DriverUserExistsResponse;
import com.dsa.internalcommon.responese.NumberCodeResponse;
import com.dsa.internalcommon.responese.TokenResponse;
import com.dsa.internalcommon.util.JwtUtils;
import com.dsa.internalcommon.util.RedisPrefixUtils;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class VerificationCodeService {

    @Autowired
    ServiceDriverUserClient serviceDriverUserClient;
    @Autowired
    ServiceVerificationCodeClient serviceVerificationCodeClient;
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    public ResponseResult checkAndSendVerificationCode(String driverPhone){
        //查询service-driver-user，该说记号是否存在
        ResponseResult<DriverUserExistsResponse> driverUserExistsResponseResponseResult = serviceDriverUserClient.getDriverUser(driverPhone);
        DriverUserExistsResponse data = driverUserExistsResponseResponseResult.getData();
        int ifExists = data.getIfexists();
        if (ifExists == DriverCarConstants.DRIVER_NOT_EXISTS){
            return ResponseResult.fail(CommonStatusEnum.DRIVER_NOT_EXISTS.getCode(), CommonStatusEnum.DRIVER_NOT_EXISTS.getValue());
        }
        log.info(driverPhone + "司机存在");
        //获取验证码
        ResponseResult<NumberCodeResponse> numberCodeResult = serviceVerificationCodeClient.getNumberCode(5);
        NumberCodeResponse numberCodeResponse = numberCodeResult.getData();
        int numberCode = numberCodeResponse.getNumberCode();
        log.info("验证码是"+numberCode);
        String key = RedisPrefixUtils.generatorKeyByPhone(driverPhone, IdentityConstants.DRIVER_IDENTITY);
        stringRedisTemplate.opsForValue().set(key, numberCode+"", 2, TimeUnit.MINUTES);
        //调用第三方验证码,阿里、腾讯、华信、容联短信服务

        //存入redis


        return ResponseResult.success("");
    }

    public ResponseResult checkCode(String driverPhone, String verificationCode){
        System.out.println("去redis读对应手机号的验证码");
        String key = RedisPrefixUtils.generatorKeyByPhone(driverPhone, IdentityConstants.DRIVER_IDENTITY);
        String codeRedis = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isBlank(codeRedis)){
            System.out.println("用户未获取验证码");
            return ResponseResult.fail(CommonStatusEnum.VERIFY_CODE_ERROR.getCode(), CommonStatusEnum.VERIFY_CODE_ERROR.getValue());
        }
        if (!codeRedis.equals(verificationCode)){
            System.out.println("验证码错误");
            return ResponseResult.fail(CommonStatusEnum.VERIFY_CODE_ERROR.getCode(), CommonStatusEnum.VERIFY_CODE_ERROR.getValue());
        }
        //司机不在数据库中不会发验证码，见driver发验证码逻辑
        System.out.println("发令牌");
        String accessToken = JwtUtils.generatorToken(driverPhone, IdentityConstants.DRIVER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);//这个1表示乘客，应该用常量
        String refreshToken = JwtUtils.generatorToken(driverPhone, IdentityConstants.DRIVER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);//这个1表示乘客，应该用常量
        //存token到redis
        String accessTokenKey = RedisPrefixUtils.generateTokenKey(driverPhone, IdentityConstants.DRIVER_IDENTITY, TokenConstants.ACCESS_TOKEN_TYPE);
        String refreshTokenKey = RedisPrefixUtils.generateTokenKey(driverPhone, IdentityConstants.DRIVER_IDENTITY, TokenConstants.REFRESH_TOKEN_TYPE);

        stringRedisTemplate.opsForValue().set(accessTokenKey, accessToken, 10, TimeUnit.DAYS);
        stringRedisTemplate.opsForValue().set(refreshTokenKey, refreshToken, 11, TimeUnit.DAYS);//refreshToken要晚过期一会

        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setAccessToken(accessToken);
        tokenResponse.setRefreshToken(refreshToken);

        return ResponseResult.success(tokenResponse);
    }
}
