package com.lontsi.rubberduckpulsarconnector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RubberduckPulsarConnectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(RubberduckPulsarConnectorApplication.class, args);

    }

}