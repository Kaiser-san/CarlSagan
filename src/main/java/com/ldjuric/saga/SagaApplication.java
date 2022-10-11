package com.ldjuric.saga;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SagaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SagaApplication.class, args);
    }

}
