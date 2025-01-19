package com.dsa.servicepassengeruser;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
//@MapperScan("com.dsa.servicepassengeruser.mapper")//有了@mapper就可以不用这个了
public class ServicePassengerUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServicePassengerUserApplication.class, args);
	}

}
