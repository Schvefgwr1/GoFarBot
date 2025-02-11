package com.example.gofarbot.controllers.web_controllers;


import com.example.gofarbot.controllers.web_controllers.dto.conferences.*;
import com.example.gofarbot.services.web_services.ConferenceService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping(path = "/api/conferences", produces = "application/json")
@AllArgsConstructor
@Slf4j
public class ConferencesController {
    private final ConferenceService conferenceService;

    @GetMapping
    public ResponseEntity<GetConferencesResponse> getConferences() {
        GetConferencesResponse response = conferenceService.getConferences();
        HttpStatus httpStatus;
        if(Objects.equals(response.getCode(), (short) 200)) {
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetConferenceResponse> getConference(@PathVariable("id") long conferenceId) {
        GetConferenceResponse response = conferenceService.getConference(conferenceId);
        HttpStatus httpStatus;
        if(Objects.equals(response.getCode(), (short) 200)) {
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }

    @PostMapping
    public ResponseEntity<CreateConferenceResponse> createConference(
            @RequestBody @Valid CreateConferenceRequest request
    ) {
        CreateConferenceResponse response = conferenceService.createConference(request);
        HttpStatus httpStatus;
        if(Objects.equals(response.getCode(), (short) 200)) {
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateConferenceResponse> updateConference(
            @PathVariable("id") long conferenceId,
            @RequestBody UpdateConferenceRequest request
    ) {
        UpdateConferenceResponse response = conferenceService.updateConference(request, conferenceId);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf((response.getCode())));
    }
}
