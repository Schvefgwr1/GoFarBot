package com.example.gofarbot.controllers.bot_controllers;

import com.example.gofarbot.config.BotConfig;
import com.example.gofarbot.config.SpecialMessages;
import com.example.gofarbot.services.bot_services.MainBotService;
import com.example.gofarbot.services.bot_services.dto.MessageServiceDTO;
import com.example.gofarbot.services.bot_services.registration.RegistrationService;
import com.example.gofarbot.services.notifications.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@AllArgsConstructor
public class MainBotController extends TelegramLongPollingBot {
    private final BotConfig botConfig;
    private final MainBotService mainBotService;
    private final RegistrationService registrationService;
    private final SpecialMessages specialMessages;
    private final NotificationService notificationService;

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
        boolean haveLinkParam = false;
        String statParam = "";
        String linkParam = "";
        String username = null; // Никнейм пользователя для проверки ресурсов

        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            long userId = update.getMessage().getFrom().getId();
            
            // Получаем никнейм пользователя из Telegram API
            username = update.getMessage().getFrom().getUserName();

            Message message = update.getMessage();
            if (message.hasText() && message.getText().startsWith("/")) {
                String messageT = message.getText();
                String[] parts = message.getText().split(" ");
                String parameter = parts.length > 1 ? parts[1] : null;

                if(parameter != null) {
                    if(parameter.startsWith("registration")) {
                        haveRegisterParam = true;
                        String[] partsParam = parameter.split("_");
                        statParam = partsParam.length > 1 ? partsParam[1] : "";
                    }
                    else {
                        if (parameter.startsWith("link")) {
                            haveLinkParam = true;
                            linkParam = parameter;
                        } else {
                            try {
                                sendMessagesForCommand(chatId, message.getText().split(" ")[0], username, statParam);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } else {
                    try {
                        sendMessagesForCommand(chatId, message.getText(), username, statParam);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                startCommandReceived(SendMessage.builder()
                        .chatId(chatId)
                        .parseMode(ParseMode.HTML)
                        .text(specialMessages.getUnsupportedCommandMessage())
                        .build()
                );
            }
        }
        if(update.hasCallbackQuery() || haveRegisterParam || haveLinkParam) {
            String call_data;
            if(!haveRegisterParam && !haveLinkParam) {
                call_data = update.getCallbackQuery().getData();
                // Получаем никнейм пользователя из callback query если он не был получен ранее
                if (username == null) {
                    username = update.getCallbackQuery().getFrom().getUserName();
                }
            }
            else {
                if(haveRegisterParam) {
                    call_data = "registration";
                }
                else {
                    call_data = linkParam;
                }
            }
            long chatId;
            if(update.hasCallbackQuery()) {
                chatId = update.getCallbackQuery().getMessage().getChatId();
            } else {
                chatId = update.getMessage().getChatId();
            }

            sendMessagesForCommand(chatId, call_data, username, statParam);
        }
    }

    // Основной метод с поддержкой проверки ресурсов
    private void sendMessagesForCommand(long chatId, String command, String username, String statParam) {
        if((Objects.equals(command, "registration")) && (!registrationService.haveActiveConference())) {
            command = "null_registration";
        }
        MessageServiceDTO messageServiceDTO;
        if(Objects.equals(command, "back_button")) {
            messageServiceDTO = mainBotService.getBackMessage(chatId);
        } else {
            if(command.startsWith("link")) {
                String[] partsParam = command.split("_");
                messageServiceDTO = partsParam.length > 1 ?
                    mainBotService.getMessageForLink(chatId, partsParam[1]) :
                    mainBotService.getStandardMessage(chatId, "/start", username, null);
            } else {
                messageServiceDTO = mainBotService.getStandardMessage(chatId, command, username, null);
            }
        }

        if(Objects.equals(command, "registration")) {
            if(registrationService.haveActiveConference()) {
                registrationService.registerUserToAllConference(chatId, statParam);
            }
            else {
                registrationService.createOldNotification(chatId);
            }
        }

        long accumulatedDelay = 0;

        while (messageServiceDTO != null) {
            final MessageServiceDTO lambdaDTO = messageServiceDTO;

            if (accumulatedDelay == 0) {
                startCommandReceived(lambdaDTO.getMessage());
            } else {
                notificationService.scheduleTask(() -> startCommandReceived(lambdaDTO.getMessage()),
                        accumulatedDelay, TimeUnit.MILLISECONDS);
            }

            if(lambdaDTO.getDelay() != null && lambdaDTO.getDelay() > 0) {
                accumulatedDelay += lambdaDTO.getDelay();
            }
            
            // Получаем следующее сообщение с передачей username и validatedResources из текущего DTO
            String nextUsername = lambdaDTO.getUsername() != null ? lambdaDTO.getUsername() : username;
            messageServiceDTO = lambdaDTO.getNextMessageId() != null
                    ? mainBotService.getStandardMessage(chatId, lambdaDTO.getNextMessageId(), nextUsername, lambdaDTO.getValidatedResources())
                    : null;
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
