package com.example.gofarbot.services.bot_services.registration;


import com.example.gofarbot.data.ConferenceRepository;
import com.example.gofarbot.data.UserRepository;
import com.example.gofarbot.models.Conference;
import com.example.gofarbot.models.User;
import com.example.gofarbot.services.notifications.NotificationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class RegistrationService {
    private final ConferenceRepository conferenceRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;

    private final String message = """
        Остались вопросы по поступлению? Приходи на бесплатную консультацию https://forms.gle/isF2behHdD1y2viG9
    """;

    public boolean haveActiveConference() {
        return !conferenceRepository.findAllAfterTime(LocalDateTime.now()).isEmpty();
    }

    public void registerUserToAllConference(long chatId) {
        List<Conference> conferences = conferenceRepository.findAllAfterTime(LocalDateTime.now());
        Optional<User> user = userRepository.findUserByChatId(chatId);
        if(user.isPresent()) {
            for(Conference conference: conferences) {
                List<User> users = conference.getUsers();
                if(!users.contains(user.get())) {
                    users.add(user.get());
                    conference.setUsers(users);
                }
            }
            conferenceRepository.saveAll(conferences);
        }
        else {
            log.warn("Don't have user with chatId in registration: {}", chatId);
        }
    }

    public void createOldNotification(long chatId) {
        long delay = LocalDateTime.now().until(LocalDateTime.now().plusHours(2), java.time.temporal.ChronoUnit.MILLIS);
        Runnable task = () -> {
            eventPublisher.publishEvent(new SendMessageEvent(this, chatId, message));
        };
        notificationService.scheduleTask(task, delay, TimeUnit.MILLISECONDS);
        log.info("Successful creating old notification to send message: {}", chatId);
    }
}
