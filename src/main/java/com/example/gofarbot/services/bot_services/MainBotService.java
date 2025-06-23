package com.example.gofarbot.services.bot_services;


import com.example.gofarbot.dto.ExceptionMessage;
import com.example.gofarbot.exceptions.BackMessageException;
import com.example.gofarbot.exceptions.UserException;
import com.example.gofarbot.services.bot_services.dto.MessageServiceDTO;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;


@Service
@Slf4j
@AllArgsConstructor
public class MainBotService {
    private final ExceptionMessage exceptionMessage;
    private final MessageService messageService;

    public MessageServiceDTO getBackMessage(long chatId) {
        try {
            return messageService.getBackMessageToUser(chatId);
        } catch (UserException e) {
            log.error(e.toString());
            return buildExceptionMessage(chatId);
        } catch (BackMessageException e) {
            log.error(e.toString());
            return this.getStandardMessage(chatId, "/start", null, null);
        }
    }

    public MessageServiceDTO getMessageForLink(long chatId, String link) {
        return handleMessage(() -> messageService.getLinkMessage(link, chatId), chatId);
    }

    // Методы с передачей накопленных результатов проверки ресурсов
    public MessageServiceDTO getStandardMessage(long chatId, String code, String username, Map<Long, Boolean> validatedResources) {
        return handleMessage(() -> messageService.getMessage(code, chatId, username, validatedResources), chatId);
    }

    public MessageServiceDTO getStandardMessage(long chatId, Long id, String username, Map<Long, Boolean> validatedResources) {
        return handleMessage(() -> messageService.getMessage(id, chatId, username, validatedResources), chatId);
    }

    private MessageServiceDTO handleMessage(MessageSupplier messageSupplier, long chatId) {
        try {
            MessageServiceDTO messageDTO = messageSupplier.get();
            if (!isValidMessageType(messageDTO.getMessage())) {
                log.error("Incorrect type of message in {}", this.getClass().getName());
                messageDTO.setMessage(exceptionMessage.getExceptionMessage(chatId));
            }
            return messageDTO;
        } catch (Exception e) {
            log.error(e.toString());
            return buildExceptionMessage(chatId);
        }
    }

    private boolean isValidMessageType(Object message) {
        return message instanceof SendMessage || message instanceof SendPhoto || message instanceof SendDocument;
    }

    private MessageServiceDTO buildExceptionMessage(long chatId) {
        return MessageServiceDTO.builder()
                .message(exceptionMessage.getExceptionMessage(chatId))
                .build();
    }
}