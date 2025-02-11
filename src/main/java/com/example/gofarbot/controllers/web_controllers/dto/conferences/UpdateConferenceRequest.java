package com.example.gofarbot.controllers.web_controllers.dto.conferences;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UpdateConferenceRequest {
    @Nullable
    private final String name;

    @Nullable
    private final String link;

    @Nullable
    private final LocalDateTime timeOfConference;
}
