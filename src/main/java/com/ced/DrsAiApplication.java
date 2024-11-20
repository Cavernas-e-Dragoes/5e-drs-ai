package com.ced;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DrsAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrsAiApplication.class, args);
    }

}
