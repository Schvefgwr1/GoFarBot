package com.example.gofarbot.services.bot_services;


import com.example.gofarbot.dto.ExceptionMessage;
import com.example.gofarbot.exceptions.BackMessageException;
import com.example.gofarbot.exceptions.UserException;
import com.example.gofarbot.services.bot_services.dto.MessageServiceDTO;
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
    private final MessageService messageService;

    public MessageServiceDTO getBackMessage(long chatId) {
        try {
            return messageService.getBackMessageToUser(chatId);
        }
        catch(UserException e) {
            log.error(e.toString());
            return MessageServiceDTO.builder()
                    .message(new ExceptionMessage(chatId))
                    .build();
        }
        catch(BackMessageException e) {
            log.error(e.toString());
            //реализовать отправку мне в чат сообщения об ошибке
            return this.getStandardMessage(chatId, "/start");
        }
    }

    public MessageServiceDTO getStandardMessage(long chatId, String code) {
        try {
            MessageServiceDTO messageDTO = messageService.getMessage(code, chatId);
            if (!(messageDTO.getMessage() instanceof SendMessage) &&
                !(messageDTO.getMessage() instanceof SendPhoto) &&
                !(messageDTO.getMessage() instanceof SendDocument)
            ) {
                log.error("Incorrect type of message in {}", this.getClass().getName());
                messageDTO.setMessage(new ExceptionMessage(chatId));
            }
            return messageDTO;
        } catch (Exception e) {
            log.error(e.toString());
            return MessageServiceDTO.builder()
                    .message(new ExceptionMessage(chatId))
                    .build();
        }
    }

    public MessageServiceDTO getStandardMessage(long chatId, Long id) {
        try {
            MessageServiceDTO messageDTO = messageService.getMessage(id, chatId);
            if (!(messageDTO.getMessage() instanceof SendMessage) &&
                !(messageDTO.getMessage() instanceof SendPhoto) &&
                !(messageDTO.getMessage() instanceof SendDocument)
            ) {
                log.error("Incorrect type of message in {}", this.getClass().getName());
                messageDTO.setMessage(new ExceptionMessage(chatId));
            }
            return messageDTO;
        } catch (Exception e) {
            log.error(e.toString());
            return MessageServiceDTO.builder()
                    .message(new ExceptionMessage(chatId))
                    .build();
        }
    }
}
