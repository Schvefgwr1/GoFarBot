package com.example.gofarbot.services.bot_services.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageServiceDTO {
    private Long nextMessageId;
    private Object message;
}
