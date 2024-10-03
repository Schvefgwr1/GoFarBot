package com.example.gofarbot.dto;

import lombok.Builder;
import lombok.Data;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


public class ExceptionMessage extends SendMessage {
    private static final String exceptionMessage = """
            Дорогой друг! В боте возникли техническое проблемы.
            Мы уже исправляем ситуацию. Попробуй через
            некоторое время еще раз ввести команду /start .
            
            Если это не поможет, обратись к команде GoFar в соц. сетях! @gofar_ru
            Спасибо, что остаешься с нами!
            """;

    public ExceptionMessage(long chatId) {
        super(String.valueOf(chatId), exceptionMessage);
    }
}
