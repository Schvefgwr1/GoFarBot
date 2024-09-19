package com.example.gofarbot.config;


import io.minio.MinioClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinioConfig {

    @Value("${minio.url}")
    private String url;

    private final String accessKey = System.getenv("MINIO_ACCESS_KEY");
    private final String secretKey = System.getenv("MINIO_SECRET_KEY");

    @Bean
    public MinioClient minioClient() {
        log.info("Minio URL: {}", url);
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

    @Value("${minio.buckets.documents}")
    @Getter
    private String documentsBucket;
    @Value("${minio.buckets.photos}")
    @Getter
    private String photosBucket;
}
