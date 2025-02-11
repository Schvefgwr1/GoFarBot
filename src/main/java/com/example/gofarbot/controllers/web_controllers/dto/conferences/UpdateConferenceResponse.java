package com.example.gofarbot.controllers.web_controllers.dto.conferences;


import com.example.gofarbot.controllers.web_controllers.dto.BaseResponse;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
public class UpdateConferenceResponse extends BaseResponse {
    private final Long id;
    private final String name;
    private final String link;
    private final LocalDateTime timeOfConference;;
}

