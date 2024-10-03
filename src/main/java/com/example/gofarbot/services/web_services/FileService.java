package com.example.gofarbot.services.web_services;

import com.example.gofarbot.config.MinioConfig;
import com.example.gofarbot.controllers.web_controllers.dto.files.GetFileResponse;
import com.example.gofarbot.controllers.web_controllers.dto.files.GetFilesResponse;
import com.example.gofarbot.controllers.web_controllers.dto.files.UploadFileRequest;
import com.example.gofarbot.controllers.web_controllers.dto.files.UploadFileResponse;
import com.example.gofarbot.data.FileRepository;
import com.example.gofarbot.exceptions.DatabaseException;
import com.example.gofarbot.exceptions.FileException;
import com.example.gofarbot.models.File;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;


@Service
@AllArgsConstructor
@Slf4j
public class FileService {
    private final MinioClient minioClient;
    private final MinioConfig minioConfig;
    private final FileRepository fileRepository;

    public UploadFileResponse uploadFile(UploadFileRequest request) {
        if(!request.isValidFile()) {
            return UploadFileResponse.builder()
                    .code((short) 500)
                    .message("Incorrect type of file")
                    .build();
        }
        byte[] decodedBytes = Base64.getDecoder().decode(request.getFileString());
        InputStream inputStream = new ByteArrayInputStream(decodedBytes);
        String bucket = minioConfig.getDocumentsBucket();
        File.FileType fileType = request.isPDF() ? File.FileType.DOCUMENT : File.FileType.PHOTO;
        if(request.isPDF()) {
            bucket = minioConfig.getDocumentsBucket();
        }
        if(request.isPNG()) {
            bucket = minioConfig.getPhotosBucket();
        }
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(request.getFileName())
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(request.getTypeOfFile())
                            .build()
            );
            Optional<File> oldFile = fileRepository.findByLink(request.getFileName());
            File saveFile = oldFile.orElseGet(() -> fileRepository.save(File.builder()
                    .type(fileType)
                    .link(request.getFileName())
                    .build()
            ));
            return UploadFileResponse.builder()
                    .code((short) 200)
                    .file(saveFile)
                    .message("Successful upload file: " + request.getFileName())
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return UploadFileResponse.builder()
                    .code((short) 500)
                    .message(e.getMessage())
                    .build();
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

    public GetFilesResponse getAllFiles() throws DatabaseException {
        try {
            return GetFilesResponse.builder()
                    .code((short) 200)
                    .message("Successful operation")
                    .files(fileRepository.findAll())
                    .build();
        }
        catch (Exception e) {
           throw new DatabaseException("Error in database", fileRepository.getClass().getName(), "Get all files");
        }
    }

    public GetFileResponse getFileById(long id) throws DatabaseException, FileException, IOException {
        try {
            File file = fileRepository.findById(id).orElseThrow(
                    () -> new DatabaseException(
                            "Empty content by id: " + id,
                            fileRepository.getClass().getName(),
                            "Get file by id"
                    )
            );
            InputFile inputFile = getFile(file.getLink(), file.getType());
            if(inputFile != null) {
                String base64File = convertInputFileToBase64(inputFile);
                return GetFileResponse.builder()
                        .base64FileValue(base64File)
                        .file(file)
                        .code((short) 200)
                        .message("Successful operation")
                        .build();
            }
            else {
                throw new FileException("Can't get file", file.getLink(), file.getType());
            }
        } catch (DatabaseException | FileException | IOException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("Error in database", fileRepository.getClass().getName(), "Get all files");
        }
    }

    private static String convertInputFileToBase64(InputFile inputFile) throws IOException {
        InputStream inputStream = inputFile.getNewMediaStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];

        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }

        byte[] fileBytes = byteArrayOutputStream.toByteArray();
        String base64String = Base64.getEncoder().encodeToString(fileBytes);
        inputStream.close();
        return base64String;
    }
}
