package com.example.gofarbot.services.bot_services.registration;


import com.example.gofarbot.data.*;
import com.example.gofarbot.exceptions.MessageException;
import com.example.gofarbot.models.*;
import com.example.gofarbot.services.notifications.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService {
    private final ConferenceRepository conferenceRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final StatRepository statRepository;
    private final UserRegistrationRepository userRegistrationRepository;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;

    public boolean haveActiveConference() {
        return !conferenceRepository.findAllAfterTime(LocalDateTime.now()).isEmpty();
    }

    @Transactional
    public void registerUserToAllConference(long chatId, String statParam) {
        List<Conference> conferences = conferenceRepository.findAllAfterTime(LocalDateTime.now());
        Optional<User> user = userRepository.findUserByChatId(chatId);
        Stat stat;
        if(!Objects.equals(statParam, "")) {
            stat = statRepository.findByCode(statParam).orElse(null);
        } else {
            stat = null;
        }
        if(user.isPresent()) {
            for(Conference conference: conferences) {
                List<User> users = conference.getUsers();
                if(!users.contains(user.get())) {
                    UserRegistration newRegistration = UserRegistration.builder()
                            .user(user.get())
                            .conferenceId(conference.getId())
                            .stat(stat)
                            .build();
                    try {
                        userRegistrationRepository.save(newRegistration);
                    } catch (Exception e) {
                        log.error(
                                "Error of saving UserRegistration with user: {}, conference: {}",
                                user.get().getUserId(),
                                conference.getId()
                        );
                    }
                    users.add(user.get());
                }
            }
        }
        else {
            log.warn("Don't have user with chatId in registration: {}", chatId);
        }
    }

    public void createOldNotification(long chatId) {
        try {
            long delay = LocalDateTime.now().until(LocalDateTime.now().plusHours(2), java.time.temporal.ChronoUnit.MILLIS);
            Message message = messageRepository.findByCode("old_notification")
                    .orElseThrow(() -> new MessageException("Message not found in DB for code: old_notification"));
            Runnable task = () -> {
                eventPublisher.publishEvent(new SendMessageEvent(this, chatId, message.getText()));
            };
            notificationService.scheduleTask(task, delay, TimeUnit.MILLISECONDS);
            log.info("Successful creating old notification to send message: {}", chatId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
