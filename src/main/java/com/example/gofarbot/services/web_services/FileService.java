package com.example.gofarbot.services.web_services;

import com.example.gofarbot.config.MinioConfig;
import com.example.gofarbot.exceptions.FileException;
import com.example.gofarbot.models.File;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.apache.commons.io.FileUtils;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.InputStream;

@Service
@AllArgsConstructor
@Slf4j
public class FileService {
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public String uploadFile() {
        try {
            // Используем ClassPathResource для доступа к файлу в папке resources
            ClassPathResource resource = new ClassPathResource("static/Resume.pdf");

            // Получаем поток данных из файла
            try (InputStream fileInputStream = resource.getInputStream()) {
                // Отправляем объект на MinIO
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(minioConfig.getDocumentsBucket()) // название бакета
                                .object("Resume.pdf") // имя файла в бакете
                                .stream(fileInputStream, fileInputStream.available(), -1) // передаем поток, его размер и -1 для неограниченного размера части
                                .contentType("application/pdf") // тип содержимого
                                .build() // строим аргументы
                );

            }

            return "Upload successful!";
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return "Upload failed!";
        }
    }

    public InputFile getFile(String name, File.FileType type) {
        InputStream stream = null;
        try {
            if(type == File.FileType.DOCUMENT) {
                stream = minioClient.getObject(GetObjectArgs
                                .builder()
                                .bucket(minioConfig.getDocumentsBucket())
                                .object(name)
                                .build());

            }
            else if(type == File.FileType.PHOTO) {
                stream = minioClient.getObject(GetObjectArgs
                        .builder()
                        .bucket(minioConfig.getPhotosBucket())
                        .object(name)
                        .build());
            }
            if(stream == null) {
                throw new FileException("Can't get file", name, type);
            }
            else {
                InputFile inputFile = new InputFile(stream, name);
                log.info("Successful open file {}", name);
                return inputFile;
            }
        }
        catch (Exception e) {
            log.error(e.toString());
            return null;
        }
    }
}
