package com.example.gofarbot.controllers.web_controllers.dto.notifications;

import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreateNotificationRequest {
    private final String text;

    @Nullable
    private final LocalDateTime time;

    @Nullable
    private final Long number;

    @Nullable
    private final Long fileId;

    @Nullable
    private final Long typeId;
}
