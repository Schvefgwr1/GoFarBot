package com.example.gofarbot.services.resources.google_form;

import com.example.gofarbot.data.ResourceRepository;
import com.example.gofarbot.exceptions.InvalidGoogleFormConfigException;
import com.example.gofarbot.models.Resource;
import com.example.gofarbot.services.resources.ResourceRule;
import lombok.AllArgsConstructor;
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
    private GoogleFormService googleFormService;

    @Setter
    private String username;

    @Autowired
    public GoogleFormFindUserRule(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
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
            return googleFormService.userExistsByTelegramUsername(username);
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
    }
}