package com.example.gofarbot.config;


import io.minio.credentials.Credentials;
import io.minio.credentials.Provider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MinioCredentialsProvider implements Provider {

    @Value("${minio.access.name}")
    private String accessKey;

    @Value("${minio.access.secret}")
    private String accessSecret;

    @Override
    public Credentials fetch() {
        return new Credentials(accessKey, accessSecret, null, null);
    }
}
