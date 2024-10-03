package com.example.gofarbot.controllers.web_controllers.dto.conferences;


import com.example.gofarbot.controllers.web_controllers.dto.BaseResponse;
import com.example.gofarbot.models.Conference;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
public class GetConferencesResponse extends BaseResponse {
    private final List<Conference> conferences;
}
