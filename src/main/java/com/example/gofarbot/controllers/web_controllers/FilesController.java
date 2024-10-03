package com.example.gofarbot.controllers.web_controllers;


import com.example.gofarbot.controllers.web_controllers.dto.files.GetFileResponse;
import com.example.gofarbot.controllers.web_controllers.dto.files.GetFilesResponse;
import com.example.gofarbot.controllers.web_controllers.dto.files.UploadFileRequest;
import com.example.gofarbot.controllers.web_controllers.dto.files.UploadFileResponse;
import com.example.gofarbot.exceptions.DatabaseException;
import com.example.gofarbot.services.notifications.DynamicNotificationService;
import com.example.gofarbot.services.web_services.FileService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(path = "/api/files", produces = "application/json")
@AllArgsConstructor
@Slf4j
public class FilesController {
    private final FileService fileService;
    private final DynamicNotificationService dynamicNotificationService;

    @PostMapping
    public ResponseEntity<UploadFileResponse> uploadFile(@RequestBody @Valid UploadFileRequest uploadFileRequest) {
        UploadFileResponse response = fileService.uploadFile(uploadFileRequest);
        HttpStatus httpStatus;
        if(Objects.equals(response.getCode(), (short) 200)) {
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }

    @GetMapping
    public ResponseEntity<GetFilesResponse> getFiles() {
        try {
            return new ResponseEntity<>(fileService.getAllFiles(), HttpStatus.OK);
        } catch (DatabaseException e) {
            log.error(e.toString());
            return new ResponseEntity<>(
                    GetFilesResponse.builder()
                            .code((short) 400)
                            .message("Unsupported error")
                            .build()
                    ,
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetFileResponse> getFileById(@PathVariable("id") long id) {
        try {
            return new ResponseEntity<>(fileService.getFileById(id), HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.toString());
            return new ResponseEntity<>(
                    GetFileResponse.builder()
                            .code((short) 400)
                            .message("Unsupported error")
                            .build()
                    ,
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
