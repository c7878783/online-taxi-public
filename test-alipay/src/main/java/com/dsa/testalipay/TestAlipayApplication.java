package com.dsa.testalipay;

import org.checkerframework.common.reflection.qual.GetClass;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class TestAlipayApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestAlipayApplication.class, args);
    }



}
