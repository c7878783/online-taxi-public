package com.dsa.internalcommon.util;

public class RedisPrefixUtils {

    public static String verificationCodePrefix = "verification-code-";
    public static String tokenPrefix = "token-";
    public static String blackDeviceCodePrefix = "black-device-";

    /**
     * @param passengerPhone 手机号码
     * @param identity 身份(司机或乘客)
     * @param tokenType token类型，access or refresh
     * @return 根据手机号码和身份生成的，用在redis中的token key
     */
    public static String generateTokenKey(String passengerPhone, String identity, String tokenType){
        return tokenPrefix + passengerPhone + "-" + identity + "-" + tokenType;
    }
    /**
     * 当有复制代码的想法时，就应该考虑抽取方法.根据手机号得到redis key
     * @param phone 手机号码
     * @param identity 司机或乘客
     * @return 根据手机号码生成的，用于在redis中存取验证码的key
     */
    public static String generatorKeyByPhone(String phone, String identity){
        return verificationCodePrefix + identity + "-" + phone;
    }
}
