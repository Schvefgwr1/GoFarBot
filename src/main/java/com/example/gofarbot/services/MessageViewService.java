package com.example.gofarbot.services;

import com.example.gofarbot.data.MessageViewRepository;
import com.example.gofarbot.models.Message;
import com.example.gofarbot.models.MessageView;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class MessageViewService {
    
    private final MessageViewRepository messageViewRepository;
    
    /**
     * Логирует просмотр сообщения пользователем
     * @param message сообщение
     * @param userChatId ID пользователя в чате
     * @return информация о просмотре (новый или обновленный)
     */
    @Transactional
    public MessageView logMessageView(Message message, Long userChatId) {
        Optional<MessageView> existingView = messageViewRepository.findByMessageIdAndUserChatId(
                message.getId(), userChatId);
        
        if (existingView.isPresent()) {
            // Увеличиваем счетчик просмотров
            messageViewRepository.incrementViewCount(message.getId(), userChatId);
            MessageView updated = existingView.get();
            updated.setViewCount(updated.getViewCount() + 1);
            updated.setLastViewedAt(LocalDateTime.now());
            
            log.debug("Updated message view for user {} and message {}, view count: {}", 
                    userChatId, message.getId(), updated.getViewCount());
            
            return updated;
        } else {
            // Создаем новый просмотр
            MessageView newView = MessageView.builder()
                    .message(message)
                    .userChatId(userChatId)
                    .viewCount(1)
                    .firstViewedAt(LocalDateTime.now())
                    .lastViewedAt(LocalDateTime.now())
                    .build();
            
            MessageView saved = messageViewRepository.save(newView);
            
            log.debug("Created new message view for user {} and message {}", 
                    userChatId, message.getId());
            
            return saved;
        }
    }
    
    /**
     * Проверяет, просматривал ли пользователь сообщение ранее
     * @param messageId ID сообщения
     * @param userChatId ID пользователя в чате
     * @return true если пользователь уже просматривал сообщение
     */
    public boolean hasUserViewedMessage(Long messageId, Long userChatId) {
        return messageViewRepository.existsByMessageIdAndUserChatId(messageId, userChatId);
    }
    
    /**
     * Проверяет нужно ли логировать просмотр для данного сообщения
     * @param message сообщение
     * @return true если нужно логировать
     */
    public boolean shouldLogView(Message message) {
        return message != null && message.isLogging();
    }
} 