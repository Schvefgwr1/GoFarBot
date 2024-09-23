package com.example.gofarbot.services.bot_services;

import com.example.gofarbot.data.DialogStateRepository;
import com.example.gofarbot.data.MessageRepository;
import com.example.gofarbot.exceptions.DialogStateException;
import com.example.gofarbot.exceptions.MessageException;
import com.example.gofarbot.models.DialogState;
import com.example.gofarbot.models.File;
import com.example.gofarbot.models.Link;
import com.example.gofarbot.models.Message;
import com.example.gofarbot.services.web_services.FileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

@Service
@AllArgsConstructor
@Slf4j
public class MessageService {
    private final DialogStateRepository dialogStateRepository;
    private final MessageRepository messageRepository;
    private final FileService fileService;

    public Object getMessage(
            DialogState.DialogStates dialogStateE, int numberMessage, long chatId
    ) throws DialogStateException, MessageException {
        Message message = this.getMessageObject(dialogStateE, numberMessage);
        String text = getTextMessage(message);
        if(message.getFile() != null) {
            InputFile inputFile = fileService.getFile(
                    message.getFile().getLink(),
                    message.getFile().getType()
            );
            if(inputFile == null) {
                log.error("Can't send file in message: {}", message.getId());
                return SendMessage.builder()
                        .chatId(chatId)
                        .parseMode(ParseMode.HTML)
                        .text(text)
                        .build();
            }
            else {
                if(message.getFile().getType() == File.FileType.DOCUMENT) {
                    return SendDocument.builder()
                            .chatId(chatId)
                            .parseMode(ParseMode.HTML)
                            .document(inputFile)
                            .caption(text)
                            .build();
                }
                else {
                    return SendPhoto.builder()
                            .chatId(chatId)
                            .parseMode(ParseMode.HTML)
                            .photo(inputFile)
                            .caption(text)
                            .build();
                }
            }
        }
        else {
            return SendMessage.builder()
                    .chatId(chatId)
                    .parseMode(ParseMode.HTML)
                    .text(text)
                    .build();
        }
    }

    public Message getMessageObject(DialogState.DialogStates dialogStateE,
                                    int numberMessage
    ) throws DialogStateException, MessageException {
        DialogState dialogState = dialogStateRepository.findDialogStateByState(dialogStateE)
                .orElseThrow(() -> new DialogStateException("Dialog not found in DB", dialogStateE));
        return messageRepository.findByNumberAndDialog(numberMessage, dialogState)
                .orElseThrow(() -> new MessageException("Message not found in DB", numberMessage, dialogState));
    }

    public String getTextMessage(Message message) {
        if (message.getLinks().isEmpty()) {
            return message.getText();
        } else {
            StringBuilder builder = new StringBuilder(message.getText() + "\n");
            for(Link link : message.getLinks()) {
                builder.append(link.getValue()).append("\n");
            }
            return builder.toString();
        }
    }
}
