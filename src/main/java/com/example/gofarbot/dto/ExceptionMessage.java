package com.example.gofarbot.dto;


import com.example.gofarbot.config.SpecialMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class ExceptionMessage {
    @Autowired
    public ExceptionMessage(SpecialMessages specialMessages) {
        exceptionMessage = specialMessages.getErrorMessage();
    }

    private final String exceptionMessage;

    public SendMessage getExceptionMessage(long chatId) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(exceptionMessage)
                .parseMode(ParseMode.HTML)
                .build();
    }
}
