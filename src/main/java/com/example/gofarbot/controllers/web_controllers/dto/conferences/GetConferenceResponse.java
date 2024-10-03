package com.example.gofarbot.controllers.web_controllers.dto.conferences;


import com.example.gofarbot.controllers.web_controllers.dto.BaseResponse;
import com.example.gofarbot.models.Conference;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class GetConferenceResponse extends BaseResponse {
    private final Conference conference;
}
