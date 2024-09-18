package com.example.gofarbot.services.bot_services.registration;

import com.example.gofarbot.controllers.bot_controllers.MainBotController;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@AllArgsConstructor
public class SendMessageEventListener {
    private final MainBotController mainBotController;

    @EventListener
    public void onSendMessageEvent(SendMessageEvent event) {
        SendMessage message = SendMessage.builder()
                        .chatId(event.getChatId())
                        .text(event.getMessage())
                        .build();

        mainBotController.startCommandReceived(message);
    }
}
