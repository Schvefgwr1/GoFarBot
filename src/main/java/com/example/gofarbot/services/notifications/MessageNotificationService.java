package com.example.gofarbot.services.notifications;


import com.example.gofarbot.controllers.bot_controllers.MainBotController;
import com.example.gofarbot.data.UserRepository;
import com.example.gofarbot.models.*;
import com.example.gofarbot.services.bot_services.KeyboardsService;
import com.example.gofarbot.services.web_services.FileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class MessageNotificationService {
    private final MainBotController mainBotController;
    private final FileService fileService;
    private final KeyboardsService keyboardsService;
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 18 7 10 *")
    public void scheduledTask() {
        if (LocalDateTime.now().getYear() == 2024) {
            Iterable<User> users = userRepository.findAll();
            users.forEach(user ->
                mainBotController.startCommandReceived(SendMessage.builder()
                        .chatId(user.getChatId())
                        .text("""
                        Остались вопросы по поступлению? Приходи на бесплатную консультацию (ссылка на анкету)
                        Если тебе интересно узнать больше о подготовке вместе с командой GoLearn, то переходи по кнопке ниже и оставляй заявку на бесплатную консультацию по
                        подготовке к поступлению у методиста (добавить кнопку "Бесплатная консультация от методиста GoLearn"
                        """)
                        .replyMarkup(keyboardsService.getLastNotificationKeyboardMarkup())
                        .build()
                )
            );
        }
    }


    public void sendConferenceNotification(Conference conference, NotificationType.NotificationTypes type) {
        List<Notification> notifications = new LinkedList<>();
        for(Notification not: conference.getNotifications()) {
            if(not.getType().getType() == type) {
               notifications.add(not);
            }
        }
        for(Notification notification: notifications) {
            if (notification != null) {
                if (notification.getFile() == null) {
                    sendTextMessage(conference.getUsers(), notification);
                } else {
                    InputFile inputFile = fileService.getFile(
                            notification.getFile().getLink(),
                            notification.getFile().getType()
                    );
                    if (inputFile == null) {
                        log.error("Can't send file");
                        sendTextMessage(conference.getUsers(), notification);
                    } else {
                        if (notification.getFile().getType() == File.FileType.DOCUMENT) {
                            sendFileMessage(conference.getUsers(), notification, inputFile);
                        } else {
                            sendPhotoMessage(conference.getUsers(), notification, inputFile);
                        }
                    }
                }
            } else {
                log.warn("Can't find notification: {} for conference: {}", type, conference.getName());
            }
        }
    }

    private String getNotificationMessage(Notification notification) {
        StringBuilder str = new StringBuilder();
        str.append(notification.getText());
        if(notification.getLinks() != null && !notification.getLinks().isEmpty()) {
            str.append("\n");
            for (Link link : notification.getLinks()) {
                str.append(link.getValue()).append("\n");
            }
        }
        return str.toString();
    }

    private void sendTextMessage(List<User> users, Notification notification) {
        for (User user : users) {
            mainBotController.startCommandReceived(
                    SendMessage.builder()
                            .chatId(user.getChatId())
                            .text(getNotificationMessage(notification))
                            .build()
            );
        }
    }

    private void sendFileMessage(List<User> users, Notification notification, InputFile file) {
        for(User user: users) {
            mainBotController.startCommandReceived(
                    SendDocument.builder()
                            .chatId(user.getChatId())
                            .document(file)
                            .caption(getNotificationMessage(notification))
                            .build()
            );
        }
    }

    private void sendPhotoMessage(List<User> users, Notification notification, InputFile file) {
        for(User user: users) {
            mainBotController.startCommandReceived(
                    SendPhoto.builder()
                            .chatId(user.getChatId())
                            .photo(file)
                            .caption(getNotificationMessage(notification))
                            .build()
            );
        }
    }
}
