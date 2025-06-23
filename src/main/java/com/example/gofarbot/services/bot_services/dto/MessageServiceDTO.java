package com.example.gofarbot.services.bot_services.dto;

import lombok.Builder;
import lombok.Data;
import jakarta.annotation.Nullable;
import java.util.Map;

@Data
@Builder
public class MessageServiceDTO {
    private Long nextMessageId;
    private Object message;
    @Nullable
    private Long delay;
    @Nullable
    private String username;
    @Nullable
    private Map<Long, Boolean> validatedResources; // Результаты проверки ресурсов для передачи по цепочке
}
