package com.example.gofarbot.controllers.web_controllers.dto.users;

import com.example.gofarbot.controllers.web_controllers.dto.BaseResponse;
import com.example.gofarbot.models.User;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
public class GetUsersByConferenceResponse extends BaseResponse {
    private List<User> users;
    private long page;
    private long limit;
}
