package com.example.gofarbot.services.resources.google_form;

import com.example.gofarbot.data.ResourceRepository;
import com.example.gofarbot.exceptions.InvalidGoogleFormConfigException;
import com.example.gofarbot.models.Resource;
import com.example.gofarbot.services.MessageViewService;
import com.example.gofarbot.services.resources.ResourceRule;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
@Slf4j
public class GoogleFormFindUserRule implements ResourceRule {
    private final ResourceRepository resourceRepository;
    private final MessageViewService messageViewService;
    private GoogleFormService googleFormService;

    @Setter
    private String username;
    
    @Setter
    private Long messageId;
    
    @Setter
    private Long userChatId;

    @Autowired
    public GoogleFormFindUserRule(ResourceRepository resourceRepository, MessageViewService messageViewService) {
        this.resourceRepository = resourceRepository;
        this.messageViewService = messageViewService;
    }

    @Override
    public boolean installService(long id) {
        String link;
        Optional<Resource> resourceContainer = resourceRepository.findById(id);
        if (resourceContainer.isPresent()) {
            link = resourceContainer.get().getLink();
        }
        else {
            link = "";
        }
        try {
            googleFormService = new GoogleFormService(link);
            return true;
        } catch (InvalidGoogleFormConfigException e) {
            log.error(e.toString());
            return false;
        }
    }

    @Override
    public boolean isChecked() {
        try {
            // Проверяем просматривал ли пользователь сообщение ранее
            if (messageId != null && userChatId != null) {
                boolean hasViewed = messageViewService.hasUserViewedMessage(messageId, userChatId);
                if (!hasViewed) {
                    // Если пользователь видит сообщение впервые, правило не срабатывает
                    log.debug("User {} viewing message {} for the first time, rule not triggered", 
                            userChatId, messageId);
                    return false;
                }
            }
            
            // Если сообщение уже просматривалось, выполняем проверку Google Form
            boolean userNotExists = googleFormService.notUserExistsByTelegramUsername(username);
            log.debug("Google Form check result for user {}: user not exists = {}", username, userNotExists);
            
            return userNotExists;
        } catch (IOException e) {
            log.error("Error checking Google Form for user {}: {}", username, e.getMessage());
            return false;
        }
    }
}