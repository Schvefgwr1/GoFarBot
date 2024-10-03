package com.example.gofarbot.controllers.web_controllers.dto.conferences;

import com.example.gofarbot.models.Notification;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CreateConferenceRequest {
    @NotNull
    private final String name;
    @NotNull
    private final String link;
    @NotNull
    private final LocalDateTime time;
}
