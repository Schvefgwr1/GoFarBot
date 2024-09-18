package com.example.gofarbot.services;


import com.example.gofarbot.config.MinioConfig;
import com.example.gofarbot.data.ConferenceRepository;
import com.example.gofarbot.models.Conference;
import com.example.gofarbot.services.notifications.DynamicNotificationService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class StartupRunner implements ApplicationRunner {
    private final DynamicNotificationService dynamicNotificationService;
    private final ConferenceRepository conferenceRepository;

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Conference> conferenceList = conferenceRepository.findAllAfterTime(LocalDateTime.now());
        for(Conference conference : conferenceList) {
            dynamicNotificationService.scheduleNotificationForConference(conference.getTimeOfConference());
        }
        log.info("Successful creating of notifications to actual conferences in  database");

        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioConfig.getDocumentsBucket())
                    .build()
            );
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioConfig.getDocumentsBucket())
                        .build()
                );
                System.out.println("Bucket created successfully");
            } else {
                System.out.println("Bucket already exists.");
            }
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
        }

        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(minioConfig.getPhotosBucket())
                    .build()
            );
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(minioConfig.getPhotosBucket())
                        .build()
                );
                System.out.println("Bucket created successfully");
            } else {
                System.out.println("Bucket already exists.");
            }
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
        }

        try {
            ClassPathResource resource = new ClassPathResource("static/Resume.pdf");
            ClassPathResource resource1 = new ClassPathResource("static/TestImage.png");

            try (InputStream fileInputStream = resource.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(minioConfig.getDocumentsBucket())
                                .object("Resume.pdf")
                                .stream(fileInputStream, fileInputStream.available(), -1)
                                .contentType("application/pdf")
                                .build()
                );

            }

            try (InputStream fileInputStream = resource1.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(minioConfig.getPhotosBucket())
                                .object("TestImage.png")
                                .stream(fileInputStream, fileInputStream.available(), -1)
                                .contentType("application/png")
                                .build()
                );

            }

            log.info("Upload successful!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Upload failed!");
        }
    }
}