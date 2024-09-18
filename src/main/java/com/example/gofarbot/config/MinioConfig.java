package com.example.gofarbot.config;


import io.minio.MinioClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public  class  MinioConfig {

    @Value("${minio.url}")
    private String url;
    @Value("${minio.access.name}")
    @Getter
    private String accessKey;
    @Value("${minio.access.secret}")
    @Getter
    private String accessSecret;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, accessSecret)
                .build();
    }

    @Value("${minio.buckets.documents}")
    @Getter
    private String documentsBucket;
    @Value("${minio.buckets.photos}")
    @Getter
    private String photosBucket;
}
