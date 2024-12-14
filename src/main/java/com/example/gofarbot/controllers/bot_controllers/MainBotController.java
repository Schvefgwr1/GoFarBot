package com.example.gofarbot.controllers.bot_controllers;

import com.example.gofarbot.config.BotConfig;
import com.example.gofarbot.config.MainBotControllerConfig;
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
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
@AllArgsConstructor
public class MainBotController extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final MainBotControllerConfig mainBotControllerConfig;
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

        if (update.hasMessage()) {

            long chatId = update.getMessage().getChatId();
            long userId = update.getMessage().getFrom().getId();

            Message message = update.getMessage();
            if (message.hasText() && message.getText().startsWith("/start")) {
                String[] parts = message.getText().split(" ");
                String parameter = parts.length > 1 ? parts[1] : null;

                if(parameter != null) {
                    if(parameter.equals("registration")) {
                        haveRegisterParam = true;
                    }
                } else {
                    try {
                        startCommandReceived(mainBotService.getStartMessage(chatId, userId));
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
            switch (mainBotControllerConfig.getValue(call_data)) {
                case 0:
                    startCommandReceived(mainBotService.getBackMessage(chatId));
                    break;
//                case 1:
//                    startCommandReceived(mainBotService.getStandardMessage(
//                            chatId,
//                            DialogState.DialogStates.INFORMATION,
//                            1)
//                    );
//                    break;
//                case 2:
//                    startCommandReceived(mainBotService.getStandardMessage(
//                            chatId,
//                            DialogState.DialogStates.INFORMATION,
//                            2
//                    ));
//                    break;
//                case 3:
//                    startCommandReceived(mainBotService.getStandardMessage(
//                            chatId,
//                            DialogState.DialogStates.INFORMATION,
//                            3
//                    ));
//                    startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
//                    break;
//                case 4:
//                    startCommandReceived(mainBotService.getStandardMessage(
//                            chatId,
//                            DialogState.DialogStates.INFORMATION,
//                            4
//                    ));
//                    startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
//                    break;
//                case 5:
//                    startCommandReceived(mainBotService.getStandardMessage(
//                            chatId,
//                            DialogState.DialogStates.INFORMATION,
//                            5
//                    ));
//                    startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
//                    break;
//                case 6:
//                    startCommandReceived(mainBotService.getStandardMessage(
//                            chatId,
//                            DialogState.DialogStates.INFORMATION,
//                            6
//                    ));
//                    startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
//                    break;
//                case 7:
//                    startCommandReceived(mainBotService.getStandardMessage(
//                            chatId,
//                            DialogState.DialogStates.INFORMATION,
//                            7
//                    ));
//                    startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
//                    break;

//                /*временное решение*/
                case 1:
                    startCommandReceived(SendMessage.builder()
                            .chatId(chatId)
                            .text("""
                                    Скоро тут появится подробная информация о поступлении и странах. Следи за обновлениями!
                                    """)
                            .build()
                    );
                    startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
                    break;

                case 8:
                    if(registrationService.haveActiveConference()) {
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
                        startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
                        registrationService.registerUserToAllConference(chatId);
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
                        startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
                    }
                    break;
                case 9:
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
                    startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
                    break;
                case 10:
                    startCommandReceived(mainBotService.getStandardMessage(
                            chatId,
                            DialogState.DialogStates.CONSULTATION,
                            1
                    ));
                    startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
                    break;
                case 11:
                    startCommandReceived(mainBotService.getStandardMessage(
                            chatId,
                            DialogState.DialogStates.CONTACTS,
                            1
                    ));
                    startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
                    break;
                case 12:
                    startCommandReceived(mainBotService.getStandardMessage(
                            chatId,
                            DialogState.DialogStates.GUIDE,
                            1
                    ));
                    startCommandReceived(mainBotService.getStandardMessage(
                            chatId,
                            DialogState.DialogStates.GUIDE,
                            2
                    ));
                    startCommandReceived(mainBotService.getStartMessage(chatId, chatId));
                    break;
            }
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
