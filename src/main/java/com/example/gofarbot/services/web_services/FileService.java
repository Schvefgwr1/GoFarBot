package com.example.gofarbot.services.bot_services;


import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@AllArgsConstructor
public class FileService {
    private final MinioClient minioClient;

    @SneakyThrows
    public String uploadFile() throws IOException {

        // Получаем файл и передаем его в поток
        FileInputStream fileInputStream = new FileInputStream("/tmp/Resume.pdf");

        // Отправляем объект на MinIO
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket("user1") // название бакета
                        .object("Resume.pdf") // имя файла в бакете
                        .stream(fileInputStream, fileInputStream.available(), -1) // передаем поток, его размер и -1 для неограниченного размера части
                        .contentType("application/pdf") // тип содержимого
                        .build() // строим аргументы
        );

        // Закрываем поток
        fileInputStream.close();

        return "Goofl";
    }
}
