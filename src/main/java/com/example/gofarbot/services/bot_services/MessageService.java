package com.example.gofarbot.services.bot_services;

import com.example.gofarbot.data.LinkMetricRepository;
import com.example.gofarbot.data.MessageRepository;
import com.example.gofarbot.data.UserRepository;
import com.example.gofarbot.exceptions.BackMessageException;
import com.example.gofarbot.exceptions.MessageException;
import com.example.gofarbot.exceptions.UserException;
import com.example.gofarbot.models.File;
import com.example.gofarbot.models.LinkMetric;
import com.example.gofarbot.models.Message;
import com.example.gofarbot.models.User;
import com.example.gofarbot.services.bot_services.dto.MessageServiceDTO;
import com.example.gofarbot.services.resources.ResourceValidationService;
import com.example.gofarbot.services.MessageViewService;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final KeyboardsService keyboardsService;
    private final UserRepository userRepository;
    private final LinkMetricRepository linkMetricRepository;
    private final ResourceValidationService resourceValidationService;
    private final MessageViewService messageViewService;

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
                return getMessageDTO(messages.get(0), chatId, null, null);
            }
        }
        else throw new UserException("User don't have correct state: ", chatId);
    }

    public MessageServiceDTO getLinkMessage(String linkName, long chatId) throws MessageException {
        List<Message> messages = messageRepository.findMessagesByLinkName(linkName);
        if(messages.isEmpty()) {
            return this.getMessage("/start", chatId, null, null);
        } else {
            if(messages.get(0).isAllowForLink()) {
                checkAndSaveUser(messages.get(0), chatId);
                linkMetricRepository.save(LinkMetric.builder()
                        .userId(chatId)
                        .linkName(linkName)
                        .build()
                );
                return getMessageDTO(messages.get(0), chatId, null, null);
            } else {
                return this.getMessage("/start", chatId, null, null);
            }
        }
    }

    // Методы с передачей накопленных результатов проверки ресурсов
    public MessageServiceDTO getMessage(long messageId, long chatId, String username, Map<Long, Boolean> inheritedValidatedResources) throws MessageException {
        Message message = this.getMessageObject(messageId);
        checkAndSaveUser(message, chatId);
        return getMessageDTO(message, chatId, username, inheritedValidatedResources);
    }

    public MessageServiceDTO getMessage(String code, long chatId, String username, Map<Long, Boolean> inheritedValidatedResources) throws MessageException {
        Message message = this.getMessageObject(code);
        checkAndSaveUser(message, chatId);
        return getMessageDTO(message, chatId, username, inheritedValidatedResources);
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

    private @NotNull MessageServiceDTO getMessageDTO(@NotNull Message message, long chatId, String username, Map<Long, Boolean> inheritedValidatedResources) {
        userRepository.updateUserState(chatId, message.getId());
        
        // Логирование просмотра сообщения если включено
        if (messageViewService.shouldLogView(message)) {
            messageViewService.logMessageView(message, chatId);
        }
        
        // Объединение наследованных и новых результатов проверки ресурсов
        Map<Long, Boolean> validatedResources = new HashMap<>();
        if (inheritedValidatedResources != null) {
            validatedResources.putAll(inheritedValidatedResources);
        }
        
        // Проверка ресурсов текущего сообщения если передан username
        if (username != null && !username.isEmpty()) {
            Map<Long, Boolean> currentMessageResources = collectValidatedResources(message, username, chatId);
            validatedResources.putAll(currentMessageResources);
        }
        
        InlineKeyboardMarkup keyboard = null;
        if(message.getButtons() != null) {
            keyboard = keyboardsService.getKeyboard(message.getButtons());
        }
        String text = message.getText();
        
        // Определяем следующее сообщение с учетом проверки ресурсов
        Long nextMessageId = username != null && !username.isEmpty() 
            ? getNextValidMessageId(message.getNextMessageId(), validatedResources)
            : message.getNextMessageId();
            
        MessageServiceDTO messageResponse = MessageServiceDTO.builder()
                .nextMessageId(nextMessageId)
                .delay(message.getDelay())
                .username(username)
                .validatedResources(validatedResources)
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

    private Map<Long, Boolean> collectValidatedResources(Message message, String username, Long userChatId) {
        Map<Long, Boolean> validatedResources = new HashMap<>();
        
        if (message.getResourcesInMessage() != null && !message.getResourcesInMessage().isEmpty()) {
            for (var resInMes : message.getResourcesInMessage()) {
                Long resourceId = resInMes.getResource().getId();
                boolean isValid = resourceValidationService.validateMessageResources(message, username, userChatId);
                validatedResources.put(resourceId, isValid);
                
                log.info("Resource ID: {} validation result: {} for user: {}", resourceId, isValid, username);
            }
        }
        
        return validatedResources;
    }
    

    
    private Long getNextValidMessageId(Long nextMessageId, Map<Long, Boolean> validatedResources) {
        if (nextMessageId == null) {
            return null;
        }
        
        try {
            Message nextMessage = getMessageObject(nextMessageId);
            
            // Проверяем должно ли следующее сообщение реагировать на ресурсы
            if (resourceValidationService.shouldSendMessageWithResourceReaction(nextMessage, validatedResources)) {
                return nextMessageId;
            } else {
                // Ищем следующее валидное сообщение в цепочке
                return getNextValidMessageId(nextMessage.getNextMessageId(), validatedResources);
            }
        } catch (MessageException e) {
            log.warn("Failed to get next message with ID: {}", nextMessageId, e);
            return null;
        }
    }
}