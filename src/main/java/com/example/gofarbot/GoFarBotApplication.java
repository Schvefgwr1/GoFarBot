package com.example.gofarbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class GoFarBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(GoFarBotApplication.class, args);
    }
}
