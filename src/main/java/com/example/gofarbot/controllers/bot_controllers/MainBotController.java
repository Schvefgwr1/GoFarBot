package com.example.gofarbot.controllers.bot_controllers;

import com.example.gofarbot.config.BotConfig;
import com.example.gofarbot.services.bot_services.MainBotService;
import com.example.gofarbot.services.bot_services.dto.MessageServiceDTO;
import com.example.gofarbot.services.bot_services.registration.RegistrationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

@Component
@Slf4j
@AllArgsConstructor
public class MainBotController extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final MainBotService mainBotService;
    private final RegistrationService registrationService;

    @Override
    public String getBotUsername() {
        return botConfig.getName();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            long userId = update.getMessage().getFrom().getId();

            sendMessagesForCommand(chatId, messageText);

        } else if(update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if(Objects.equals(call_data, "back_button")) {
                sendMessagesForCommand(chatId, "back_button");
                return;
            }

            if(Objects.equals(call_data, "registration")) {
                if(registrationService.haveActiveConference()) {
                    registrationService.registerUserToAllConference(chatId);
                }
                else {
                    call_data = "null_registration";
                    registrationService.createOldNotification(chatId);
                }
            }

            sendMessagesForCommand(chatId, call_data);
        }

    }

    private void sendMessagesForCommand(long chatId, String command) {
        MessageServiceDTO messageServiceDTO;
        if(Objects.equals(command, "back_button")) {
            messageServiceDTO = mainBotService.getBackMessage(chatId);
        }
        else {
            messageServiceDTO = mainBotService.getStandardMessage(chatId, command);
        }
        startCommandReceived(messageServiceDTO.getMessage());
        while(messageServiceDTO.getNextMessageId() != null) {
            messageServiceDTO = mainBotService.getStandardMessage(chatId, messageServiceDTO.getNextMessageId());
            startCommandReceived(messageServiceDTO.getMessage());
        }
    }

    public Message executeDocument(SendDocument sendDocument) throws TelegramApiException {
        return execute(sendDocument);
    }

    public Message executePhoto(SendPhoto sendPhoto) throws TelegramApiException {
        return execute(sendPhoto);
    }

    public void startCommandReceived(Object command) {
        try {
            if (command instanceof SendMessage) {
                execute((SendMessage) command); // Вызов для SendMessage
            } else if (command instanceof SendDocument) {
                execute((SendDocument) command); // Вызов для SendDocument
            } else if (command instanceof SendPhoto) {
                execute((SendPhoto) command); // Вызов для SendPhoto
            } else {
                log.error("Unsupported command type: {}", command.getClass().getName());
            }
        } catch (TelegramApiException e) {
            log.error("TelegramApiException in message received : {}", e.toString());
        }
    }
}
