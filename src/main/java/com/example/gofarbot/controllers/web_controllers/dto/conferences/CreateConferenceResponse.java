package com.example.gofarbot.controllers.web_controllers.dto.conferences;

import com.example.gofarbot.controllers.web_controllers.dto.BaseResponse;
import com.example.gofarbot.models.Conference;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class CreateConferenceResponse extends BaseResponse {
    private final Conference conference;
}
