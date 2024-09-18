package com.example.gofarbot.controllers.web_controllers;


import com.example.gofarbot.services.notifications.DynamicNotificationService;
import com.example.gofarbot.services.web_services.FileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping(path = "/api/files", produces = "application/json")
@AllArgsConstructor
public class FilesController {
    private final FileService fileService;
    private final DynamicNotificationService dynamicNotificationService;

    @GetMapping
    public ResponseEntity<String> uploadFile() {
        String response = fileService.uploadFile();
        HttpStatus httpStatus;
        if(Objects.equals(response, "Upload successful!")) {
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        LocalDateTime specificTime = LocalDateTime.of(LocalDateTime.now().toLocalDate(), LocalDateTime.of(1, 1, 1, 14, 24).toLocalTime());

        dynamicNotificationService.scheduleNotificationForConference(specificTime);
        return new ResponseEntity<String>("Uffuu", HttpStatus.OK);
    }
}
