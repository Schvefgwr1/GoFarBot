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

//        try {
//            ClassPathResource resource1 = new ClassPathResource("static/Resume.pdf");
//            ClassPathResource resource2 = new ClassPathResource("static/TestImage.png");
//            ClassPathResource resource3 = new ClassPathResource("static/01.png");
//            ClassPathResource resource4 = new ClassPathResource("static/02.png");
//            ClassPathResource resource5 = new ClassPathResource("static/03.png");
//
//
//            try (InputStream fileInputStream = resource1.getInputStream()) {
//                minioClient.putObject(
//                        PutObjectArgs.builder()
//                                .bucket(minioConfig.getDocumentsBucket())
//                                .object("Resume.pdf")
//                                .stream(fileInputStream, fileInputStream.available(), -1)
//                                .contentType("application/pdf")
//                                .build()
//                );
//            }
//            try (InputStream fileInputStream = resource2.getInputStream()) {
//                minioClient.putObject(
//                        PutObjectArgs.builder()
//                                .bucket(minioConfig.getPhotosBucket())
//                                .object("TestImage.png")
//                                .stream(fileInputStream, fileInputStream.available(), -1)
//                                .contentType("application/png")
//                                .build()
//                );
//            }
//            try (InputStream fileInputStream = resource3.getInputStream()) {
//                minioClient.putObject(
//                        PutObjectArgs.builder()
//                                .bucket(minioConfig.getPhotosBucket())
//                                .object("01.png")
//                                .stream(fileInputStream, fileInputStream.available(), -1)
//                                .contentType("application/png")
//                                .build()
//                );
//            }
//            try (InputStream fileInputStream = resource4.getInputStream()) {
//                minioClient.putObject(
//                        PutObjectArgs.builder()
//                                .bucket(minioConfig.getPhotosBucket())
//                                .object("02.png")
//                                .stream(fileInputStream, fileInputStream.available(), -1)
//                                .contentType("application/png")
//                                .build()
//                );
//            }
//            try (InputStream fileInputStream = resource4.getInputStream()) {
//                minioClient.putObject(
//                        PutObjectArgs.builder()
//                                .bucket(minioConfig.getPhotosBucket())
//                                .object("03.png")
//                                .stream(fileInputStream, fileInputStream.available(), -1)
//                                .contentType("application/png")
//                                .build()
//                );
//            }
//
//            log.info("Upload successful!");
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            log.error("Upload failed!");
//        }
    }
}