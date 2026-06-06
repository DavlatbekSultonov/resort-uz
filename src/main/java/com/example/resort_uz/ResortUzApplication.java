package com.example.resort_uz;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ResortUzApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResortUzApplication.class, args);
    }

}
