package com.example.gofarbot.services.bot_services.registration;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class SendMessageEvent extends ApplicationEvent {
    private final long chatId;
    private final String message;

    public SendMessageEvent(Object source, long chatId, String message) {
        super(source);
        this.chatId = chatId;
        this.message = message;
    }
}

