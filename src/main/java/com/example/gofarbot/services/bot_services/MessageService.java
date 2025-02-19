package com.example.gofarbot.services.bot_services;

import com.example.gofarbot.data.MessageRepository;
import com.example.gofarbot.data.UserRepository;
import com.example.gofarbot.exceptions.BackMessageException;
import com.example.gofarbot.exceptions.MessageException;
import com.example.gofarbot.exceptions.UserException;
import com.example.gofarbot.models.File;
import com.example.gofarbot.models.Message;
import com.example.gofarbot.models.User;
import com.example.gofarbot.services.bot_services.dto.MessageServiceDTO;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final KeyboardsService keyboardsService;
    private final UserRepository userRepository;

    public MessageServiceDTO getBackMessageToUser(long chatId) throws UserException, BackMessageException {
        User user = userRepository.findUserByChatId(chatId)
                .orElseThrow(() -> new UserException("User isn't found in DB with id: ", chatId));
        if(user.getMessage() != null) {
            String actualCode = user.getMessage().getCode();
            while(actualCode == null) {
                String finalActualCode = actualCode;
                Message message = messageRepository.findPreviousMessageInChain(user.getMessage().getId())
                        .orElseThrow(() -> new BackMessageException(
                                "Can't find previous message in chain to message: ",
                                user.getMessage().getId(),
                                finalActualCode,
                                0
                        ));
                actualCode = message.getCode();
            }
            List<Message> messages = messageRepository.findBackMessages(actualCode);
            if(messages.size() != 1) {
                throw new BackMessageException(
                        "Can't find individual back message to message: ",
                        user.getMessage().getId(),
                        actualCode,
                        messages.size()
                );
            }
            else {
                return getMessageDTO(messages.get(0), chatId);
            }
        }
        else throw new UserException("User don't have correct state: ", chatId);
    }

    public MessageServiceDTO getMessage(long messageId, long chatId) throws MessageException {
        Message message = this.getMessageObject(messageId);
        checkAndSaveUser(message, chatId);
        return getMessageDTO(message, chatId);
    }

    public MessageServiceDTO getMessage(String code, long chatId) throws MessageException {
        Message message = this.getMessageObject(code);
        checkAndSaveUser(message, chatId);
        return getMessageDTO(message, chatId);
    }

    private void checkAndSaveUser(Message message, long chatID) {
        try {
            Optional<User> optionalUser = userRepository.findUserByChatId(chatID);

            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setMessage(message);
                userRepository.save(user);
            } else {
                User newUser = User.builder()
                        .userId(chatID)
                        .chatId(chatID)
                        .message(message)
                        .build();
                userRepository.save(newUser);
            }
        } catch (Exception e) {
            log.error("Error in DB: {}", e.getMessage());
        }
    }


    private @NotNull MessageServiceDTO getMessageDTO(@NotNull Message message, long chatId) {
        userRepository.updateUserState(chatId, message.getId());
        InlineKeyboardMarkup keyboard = null;
        if(message.getButtons() != null) {
            keyboard = keyboardsService.getKeyboard(message.getButtons());
        }
        String text = getTextMessage(message);
        Long nextMessageId = message.getNextMessageId();
        MessageServiceDTO messageResponse = MessageServiceDTO.builder()
                .nextMessageId(nextMessageId)
                .delay(message.getDelay())
                .build();
        if(message.getFile() != null) {
            if (message.getFile().getFileId() != null) {
                if (message.getFile().getType() == File.FileType.DOCUMENT) {
                    SendDocument sendDocument =  SendDocument.builder()
                            .chatId(chatId)
                            .parseMode(ParseMode.HTML)
                            .document(new InputFile(message.getFile().getFileId()))
                            .caption(text)
                            .build();

                    if(keyboard != null) {
                        sendDocument.setReplyMarkup(keyboard);
                    }
                    messageResponse.setMessage(sendDocument);
                } else {
                    SendPhoto sendPhoto =  SendPhoto.builder()
                            .chatId(chatId)
                            .parseMode(ParseMode.HTML)
                            .photo(new InputFile(message.getFile().getFileId()))
                            .caption(text)
                            .build();
                    if(keyboard != null) {
                        sendPhoto.setReplyMarkup(keyboard);
                    }
                    messageResponse.setMessage(sendPhoto);
                }
            } else {
                log.warn("Not indexing file: {}", message.getFile());
                SendMessage sendMessage = SendMessage.builder()
                        .chatId(chatId)
                        .parseMode(ParseMode.HTML)
                        .text(text)
                        .disableWebPagePreview(!message.isHavePreview())
                        .build();
                if(keyboard != null) {
                    sendMessage.setReplyMarkup(keyboard);
                }
                messageResponse.setMessage(sendMessage);
            }
        }
        else {
            SendMessage sendMessage =  SendMessage.builder()
                    .chatId(chatId)
                    .parseMode(ParseMode.HTML)
                    .text(text)
                    .disableWebPagePreview(!message.isHavePreview())
                    .build();
            if(keyboard != null) {
                sendMessage.setReplyMarkup(keyboard);
            }
            messageResponse.setMessage(sendMessage);
        }
        return messageResponse;
    }

    private Message getMessageObject(String code) throws MessageException {
        return messageRepository.findByCode(code)
                .orElseThrow(() -> new MessageException("Message not found in DB for code: " + code));
    }

    private Message getMessageObject(Long messageId) throws MessageException {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new MessageException("Message not found in DB for id: " + messageId));
    }

    @Contract(pure = true)
    private @NotNull String getTextMessage(@NotNull Message message) {
        return message.getText();
    }
}
