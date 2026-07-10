package com.theking.theking_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.theking.theking_backend"}) // 明确指定扫描这个包及其子包
public class ThekingBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThekingBackendApplication.class, args);
    }
}