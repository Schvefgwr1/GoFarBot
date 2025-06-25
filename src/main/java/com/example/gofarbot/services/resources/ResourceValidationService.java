package com.example.gofarbot.services.resources;

import com.example.gofarbot.models.Message;
import com.example.gofarbot.models.ResInMes;
import com.example.gofarbot.models.Resource;
import com.example.gofarbot.models.ResType;
import com.example.gofarbot.services.resources.google_form.GoogleFormFindUserRule;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class ResourceValidationService {
    
    private final GoogleFormFindUserRule googleFormFindUserRule;

    /**
     * Проверяет все ресурсы прикрепленные к сообщению с передачей ID пользователя
     * @param message сообщение для проверки
     * @param username имя пользователя Telegram
     * @param userChatId ID пользователя в чате
     * @return true если все ресурсы прошли проверку или нет ресурсов для проверки
     */
    public boolean validateMessageResources(Message message, String username, Long userChatId) {
        if (message.getResourcesInMessage() == null || message.getResourcesInMessage().isEmpty()) {
            return true; // Нет ресурсов для проверки
        }
        
        log.info("Validating resources for message ID: {} and user: {}", message.getId(), username);
        
        for (ResInMes resInMes : message.getResourcesInMessage()) {
            Resource resource = resInMes.getResource();
            if (!validateResource(resource, username, message.getId(), userChatId)) {
                log.warn("Resource validation failed for resource ID: {} and user: {}", resource.getId(), username);
                return false;
            }
        }
        
        log.info("All resources validated successfully for message ID: {} and user: {}", message.getId(), username);
        return true;
    }

    /**
     * Проверяет отдельный ресурс с дополнительными параметрами
     * @param resource ресурс для проверки
     * @param username имя пользователя Telegram
     * @param messageId ID сообщения
     * @param userChatId ID пользователя в чате
     * @return true если ресурс прошел проверку
     */
    private boolean validateResource(Resource resource, String username, Long messageId, Long userChatId) {
        ResourceRule rule = getResourceRule(resource.getType());
        if (rule == null) {
            log.warn("No validation rule found for resource type: {}", resource.getType().getId());
            return true; // Если нет правила, считаем что проверка пройдена
        }
        
        try {
            if (rule.installService(resource.getId())) {
                if (rule instanceof GoogleFormFindUserRule googleFormRule) {
                    googleFormRule.setUsername(username);
                    googleFormRule.setMessageId(messageId);
                    googleFormRule.setUserChatId(userChatId);
                }
                return rule.isChecked();
            } else {
                log.error("Failed to install service for resource ID: {}", resource.getId());
                return false;
            }
        } catch (Exception e) {
            log.error("Error validating resource ID: {}", resource.getId(), e);
            return false;
        }
    }
    
    /**
     * Получает правило проверки для типа ресурса
     * @param resType тип ресурса
     * @return правило проверки или null если не найдено
     */
    private ResourceRule getResourceRule(ResType resType) {
        // Здесь можно добавить логику определения типа ресурса
        // Пока что только для Google Forms
        return googleFormFindUserRule;
    }
    
    /**
     * Проверяет нужно ли реагировать на ресурс в сообщении
     * @param message сообщение для проверки
     * @param checkedResources карта проверенных ресурсов
     * @return true если сообщение должно быть отправлено
     */
    public boolean shouldSendMessageWithResourceReaction(Message message, Map<Long, Boolean> checkedResources) {
        if (message.getReactionsToResources() == null || message.getReactionsToResources().isEmpty()) {
            return true; // Нет реакций на ресурсы
        }
        
        // Проверяем все ресурсы на которые реагирует сообщение
        for (var reaction : message.getReactionsToResources()) {
            Long resourceId = reaction.getResource().getId();
            Boolean isResourceValid = checkedResources.get(resourceId);
            
            if (isResourceValid == null || !isResourceValid) {
                log.info("Message ID: {} will be skipped due to failed resource validation for resource ID: {}", 
                        message.getId(), resourceId);
                return false;
            }
        }
        
        return true;
    }
} 