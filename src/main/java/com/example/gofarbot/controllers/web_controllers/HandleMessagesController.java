package com.example.gofarbot.controllers.web_controllers;


import com.example.gofarbot.controllers.web_controllers.dto.handle_messages.SendHandleMessageToAllUsersRequest;
import com.example.gofarbot.controllers.web_controllers.dto.handle_messages.SendHandleMessageToRegUsersRequest;
import com.example.gofarbot.controllers.web_controllers.dto.handle_messages.SendHandleMessageToUsersResponse;
import com.example.gofarbot.services.web_services.HandleMessageService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(path = "/api/handle_message", produces = "application/json")
@AllArgsConstructor
@Slf4j
public class HandleMessagesController {
    private final HandleMessageService handleMessageService;

    @PostMapping("/reg")
    public ResponseEntity<SendHandleMessageToUsersResponse> sendHandleMessageToRegUsers(
            @RequestBody @Valid SendHandleMessageToRegUsersRequest request
    ) {
        SendHandleMessageToUsersResponse response = handleMessageService.sendToRegUsers(request);
        HttpStatus httpStatus;
        if(Objects.equals(response.getCode(), (short) 200)) {
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }

    @PostMapping("/all")
    public ResponseEntity<SendHandleMessageToUsersResponse> sendHandleMessageToAllUsers(
            @RequestBody @Valid SendHandleMessageToAllUsersRequest request
    ) {
        SendHandleMessageToUsersResponse response = handleMessageService.sendToAllUsers(request);
        HttpStatus httpStatus;
        if(Objects.equals(response.getCode(), (short) 200)) {
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }
}
