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
        boolean haveRegisterParam = false;

        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            long userId = update.getMessage().getFrom().getId();

            Message message = update.getMessage();
            if (message.hasText() && message.getText().startsWith("/start")) {
                String messageT = message.getText();
                String[] parts = message.getText().split(" ");
                String parameter = parts.length > 1 ? parts[1] : null;

                if(parameter != null) {
                    if(parameter.equals("registration")) {
                        haveRegisterParam = true;
                    }
                } else {
                    try {
                        sendMessagesForCommand(chatId, message.getText());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                startCommandReceived(SendMessage.builder()
                        .chatId(chatId)
                        .text("Неправильная команда. Введите /start .")
                        .build()
                );
            }
        }
        if(update.hasCallbackQuery() || haveRegisterParam) {
            String call_data;
            if(!haveRegisterParam) {
                call_data = update.getCallbackQuery().getData();
            }
            else {
                call_data = "registration";
            }
            long chatId;
            if(update.hasCallbackQuery()) {
                chatId = update.getCallbackQuery().getMessage().getChatId();
            } else {
                chatId = update.getMessage().getChatId();
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
