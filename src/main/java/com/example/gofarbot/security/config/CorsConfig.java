package com.example.gofarbot.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Разрешить CORS для всех путей
                        .allowedOrigins("http://localhost:3000", "http://localhost:3001") // Разрешить только с localhost:3000 и localhost:3001
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Разрешить определенные HTTP методы
                        .allowedHeaders("*") // Разрешить любые заголовки
                        .allowCredentials(true); // Разрешить передачу учетных данных (куки)
            }
        };
    }
}
