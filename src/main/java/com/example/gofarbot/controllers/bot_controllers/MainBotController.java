package com.example.gofarbot.controllers.bot_controllers;

import com.example.gofarbot.config.BotConfig;
import com.example.gofarbot.models.DialogState;
import com.example.gofarbot.services.bot_services.MainBotService;
import com.example.gofarbot.services.bot_services.registration.RegistrationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

            switch (messageText) {
                case "/start":
                    try {
                        startCommandReceived(mainBotService.getStartMessage(chatId, userId));
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    break;
                default:
                    startCommandReceived(SendMessage.builder()
                            .chatId(chatId)
                            .text("Неправильная команда. Введите /start .")
                            .build()
                    );

            }
        } else if(update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long chatId = update.getCallbackQuery().getMessage().getChatId();
            switch (call_data) {
                case "back_button":
                    startCommandReceived(mainBotService.getBackMessage(chatId));
                    break;
                case "information":
                    startCommandReceived(mainBotService.getStandardMessage(
                            chatId,
                            DialogState.DialogStates.INFORMATION,
                            1)
                    );
                    break;
                case "information_countries":
                    startCommandReceived(mainBotService.getStandardMessage(
                            chatId,
                            DialogState.DialogStates.INFORMATION,
                            2
                    ));
                    break;
                case "information_austria":
                    startCommandReceived(mainBotService.getStandardMessage(
                            chatId,
                            DialogState.DialogStates.INFORMATION,
                            3
                    ));
                    startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
                    break;
                case "information_germany":
                    startCommandReceived(mainBotService.getStandardMessage(
                            chatId,
                            DialogState.DialogStates.INFORMATION,
                            4
                    ));
                    startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
                    break;
                case "information_italy":
                    startCommandReceived(mainBotService.getStandardMessage(
                            chatId,
                            DialogState.DialogStates.INFORMATION,
                            5
                    ));
                    startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
                    break;
                case "information_contacts":
                    startCommandReceived(mainBotService.getStandardMessage(
                            chatId,
                            DialogState.DialogStates.INFORMATION,
                            6
                    ));
                    startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
                    break;
                case "information_question":
                    startCommandReceived(mainBotService.getStandardMessage(
                            chatId,
                            DialogState.DialogStates.INFORMATION,
                            7
                    ));
                    startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
                    break;
                case "registration":
                    if(registrationService.haveActiveConference()) {
                        registrationService.registerUserToAllConference(chatId);
                        startCommandReceived(mainBotService.getStandardMessage(
                                chatId,
                                DialogState.DialogStates.REGISTRATION,
                                1
                        ));
                        startCommandReceived(mainBotService.getStandardMessage(
                                chatId,
                                DialogState.DialogStates.REGISTRATION,
                                2
                        ));
                        startCommandReceived(mainBotService.getStandardMessage(
                                chatId,
                                DialogState.DialogStates.REGISTRATION,
                                3
                        ));

                    }
                    else {
                        startCommandReceived(mainBotService.getStandardMessage(
                                chatId,
                                DialogState.DialogStates.REGISTRATION,
                                4
                        ));
                        startCommandReceived(mainBotService.getStandardMessage(
                                chatId,
                                DialogState.DialogStates.REGISTRATION,
                                5
                        ));
                        startCommandReceived(mainBotService.getStandardMessage(
                                chatId,
                                DialogState.DialogStates.REGISTRATION,
                                6
                        ));
                        registrationService.createOldNotification(chatId);
                    }
                    break;
                case "search_repeat":
                    startCommandReceived(mainBotService.getStandardMessage(
                            chatId,
                            DialogState.DialogStates.REGISTRATION,
                            4
                    ));
                    startCommandReceived(mainBotService.getStandardMessage(
                            chatId,
                            DialogState.DialogStates.REGISTRATION,
                            5
                    ));
                    startCommandReceived(mainBotService.getStandardMessage(
                            chatId,
                            DialogState.DialogStates.REGISTRATION,
                            6
                    ));
                    break;
            }
        }

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
