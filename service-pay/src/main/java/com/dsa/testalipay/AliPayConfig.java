package com.dsa.testalipay;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.Config;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.apache.logging.log4j.status.StatusLogger;
import org.springframework.boot.autoconfigure.http.client.HttpClientProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.ObjectInputFilter;

@Component
@ConfigurationProperties(prefix = "alipay")
@Data
public class AliPayConfig {

    private String appId;

    private String appPrivateKey;

    private String publicKey;

    private String notifyUrl;

    @PostConstruct
    public void init(){
        Config config = new Config();
        // 基础配置
        config.protocol = "https";
        config.gatewayHost = "openapi-sandbox.dl.alipaydev.com";
        config.signType="RSA2";

        //业务配置
        config.appId = this.appId;
        config.merchantPrivateKey = this.appPrivateKey;
        config.alipayPublicKey = this.publicKey;
        config.notifyUrl = this.notifyUrl;

        Factory.setOptions(config);
        System.out.println("支付宝配置初始化完成");

    }

}
