package com.example.gofarbot.config;


import io.minio.credentials.Credentials;
import io.minio.credentials.Provider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MinioCredentialsProvider implements Provider {

    private final String accessKey = System.getenv("MINIO_ROOT_USER");

    private final String accessSecret = System.getenv("MINIO_ROOT_PASSWORD");

    @Override
    public Credentials fetch() {
        return new Credentials(accessKey, accessSecret, null, null);
    }
}
