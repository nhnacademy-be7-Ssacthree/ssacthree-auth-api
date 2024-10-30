package com.nhnacademy.ssacthree_auth_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SsacthreeAuthApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsacthreeAuthApiApplication.class, args);
    }

}
