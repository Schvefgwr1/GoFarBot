package com.example.gofarbot.services.bot_services;


import com.example.gofarbot.data.UserRepository;
import com.example.gofarbot.dto.ExceptionMessage;
import com.example.gofarbot.models.DialogState.DialogStates;
import com.example.gofarbot.models.Message;
import com.example.gofarbot.models.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Optional;


@Service
@Slf4j
@AllArgsConstructor
public class MainBotService {
    private final UserRepository userRepository;
    private final MessageService messageService;
    private final KeyboardsService keyboardsService;

    public SendMessage getStartMessage(long chatId, long userId) {
        User user;
        Optional<User> userCont = userRepository.findUserByUserId(userId);
        try {
            Message message = messageService.getMessageObject(DialogStates.FIRST_MESSAGE, 1);
            user = userCont.orElseGet(() -> {
                return userRepository.save(User.builder()
                        .userId(userId)
                        .chatId(chatId)
                        .message(message)
                        .build()
                );
            });
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(messageService.getTextMessage(message))
                    .replyMarkup(keyboardsService.getKeyboard(DialogStates.FIRST_MESSAGE, 1))
                    .build();

        } catch (Exception e) {
            log.error(e.toString());
            return new ExceptionMessage(chatId);
        }
    }

    public Object getStandardMessage(long chatId, DialogStates dialogStateE, int numberMessage) {
        try {
            Object message = messageService.getMessage(dialogStateE, numberMessage, chatId);
            userRepository.updateUserState(chatId, dialogStateE.name(), numberMessage);
            if(message instanceof SendMessage ||
               message instanceof SendPhoto ||
               message instanceof SendDocument
            ) {
                InlineKeyboardMarkup keyboardMarkup = keyboardsService.getKeyboard(
                        dialogStateE, numberMessage
                );
                if(keyboardMarkup != null) {
                    return addKeyboardToMessage(message, keyboardMarkup, chatId);
                }
                else {
                    return message;
                }
            }
            else {
                log.error("Incorrect type of message in {}", this.getClass().getName());
                return new ExceptionMessage(chatId);
            }
        } catch (Exception e) {
            log.error(e.toString());
            return new ExceptionMessage(chatId);
        }
    }

    private Object addKeyboardToMessage(Object message, InlineKeyboardMarkup keyboardMarkup, long chatId) {
        if(message instanceof SendMessage sendMessage) {
            sendMessage.setReplyMarkup(keyboardMarkup);
            return sendMessage;
        }
        if(message instanceof SendPhoto sendPhoto) {
            sendPhoto.setReplyMarkup(keyboardMarkup);
            return sendPhoto;
        }
        if(message instanceof SendDocument sendDocument) {
            sendDocument.setReplyMarkup(keyboardMarkup);
            return sendDocument;
        }
        else {
            log.error("Incorrect type of message in {}", this.getClass().getName());
            return new ExceptionMessage(chatId);
        }
    }

    public SendMessage getBackMessage(long chatId) {
        try {
            User user = userRepository.findUserByChatId(chatId)
                    .orElseThrow(() -> new Exception("Error of user repository"));
            Message message;
            if(user.getMessage().getNumber() == 1) {
                userRepository.updateUserState(chatId, DialogStates.FIRST_MESSAGE.name(), 1);
                message = messageService.getMessageObject(DialogStates.FIRST_MESSAGE, 1);
            }
            else {
                message = messageService.getMessageObject(
                        user.getMessage().getDialog().getState(),
                        user.getMessage().getNumber() - 1
                );
            }
            user.setMessage(message);
            userRepository.save(user);
            return SendMessage.builder()
                    .chatId(chatId)
                    .text(message.getText())
                    .replyMarkup(keyboardsService.getKeyboard(message.getDialog().getState(), message.getNumber()))
                    .build();
        }
        catch (Exception e) {
            log.error(e.getMessage());
            return new ExceptionMessage(chatId);
        }
    }
}
