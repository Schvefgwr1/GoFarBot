package com.example.gofarbot.services.notifications;


import com.example.gofarbot.controllers.bot_controllers.MainBotController;
import com.example.gofarbot.data.FileRepository;
import com.example.gofarbot.models.*;
import com.example.gofarbot.services.web_services.FileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class MessageNotificationService {
    private final MainBotController mainBotController;
    private final FileService fileService;
    private final FileRepository fileRepository;

    public void sendPlaningConferenceNotification(Conference conference, NotificationType.NotificationTypes type) {
        List<Notification> notifications = new LinkedList<>();
        for(Notification not: conference.getNotifications()) {
            if(not.getType().getType() == type) {
               notifications.add(not);
            }
        }

        // Сортировка по свойству number
        notifications.sort((o1, o2) -> {
            if((o1.getNumber() == null) && (o2.getNumber() == null)) {
                return 1;
            }
            if(o1.getNumber() == null) {
                return -1;
            }
            if(o2.getNumber() == null) {
                return 1;
            }
            return Long.compare(o1.getNumber(), o2.getNumber());
        });

        sendNotificationsToUsers(conference, notifications, type);
    }

    public void sendHandleConferenceNotification(Conference conference, LocalDateTime notificationTime) {
        List<Notification> notifications = new LinkedList<>();
        for(Notification not: conference.getNotifications()) {
            if(Objects.requireNonNull(not.getType()).getType() == NotificationType.NotificationTypes.IN_HANDLE_TIME &&
               Objects.requireNonNull(not.getTime()).isEqual(notificationTime)
            ) {
                notifications.add(not);
            }
        }
        sendNotificationsToUsers(conference, notifications, NotificationType.NotificationTypes.IN_HANDLE_TIME);
    }

    private void sendNotificationsToUsers(
            Conference conference,
            List<Notification> notifications,
            NotificationType.NotificationTypes type
    ) {
        Hibernate.initialize(conference.getRegistrations());
        for(Notification notification: notifications) {
            if (notification != null) {
                if (notification.getFile() == null) {
                    sendTextMessage(conference.getUsers(), notification);
                } else {
                    if(notification.getFile().getFileId() != null) {
                        if (notification.getFile().getType() == File.FileType.DOCUMENT) {
                            sendFileMessage(conference.getUsers(), notification, notification.getFile().getFileId());
                        } else {
                            sendPhotoMessage(conference.getUsers(), notification, notification.getFile().getFileId());
                        }
                    }
                    else {
                        File file = notification.getFile();
                        InputFile inputFile = fileService.getFile(
                                notification.getFile().getLink(),
                                notification.getFile().getType()
                        );
                        if (inputFile == null) {
                            log.error("Can't send file: {}", notification.getFile());
                            sendTextMessage(conference.getUsers(), notification);
                            return;
                        }
                        try {
                            String fileId;
                            Long fatherUserId = 411240604L;
                            if (file.getType() == File.FileType.DOCUMENT) {
                                org.telegram.telegrambots.meta.api.objects.Message message = mainBotController.executeDocument(SendDocument.builder()
                                        .chatId(fatherUserId)
                                        .document(inputFile)
                                        .build()
                                );
                                fileId = message.getDocument().getFileId();
                            }
                            else {
                                org.telegram.telegrambots.meta.api.objects.Message message = mainBotController.executePhoto(SendPhoto.builder()
                                        .chatId(fatherUserId)
                                        .photo(inputFile)
                                        .build()
                                );
                                fileId = message.getPhoto().get(0).getFileId();
                            }
                            file.setFileId(fileId);
                            File saveFile = fileRepository.save(file);
                            log.info("Successful indexing file: {}", saveFile);
                            if (saveFile.getType() == File.FileType.DOCUMENT) {
                                sendFileMessage(conference.getUsers(), notification, fileId);
                            } else {
                                sendPhotoMessage(conference.getUsers(), notification, fileId);
                            }
                        } catch (TelegramApiException e) {
                            log.error("Error of tg API in startupRunner: {}", e.getMessage());
                        } catch (Exception e) {
                            log.error("Unsupported exception: {}", e.getMessage());
                        }
                    }
                }
            } else {
                log.warn("Can't find notification: {} for conference: {}", type, conference.getName());
            }
        }
    }

    private String getNotificationMessage(Notification notification) {
        return notification.getText();
    }

    private void sendTextMessage(List<User> users, Notification notification) {
        for (User user : users) {
            mainBotController.startCommandReceived(
                    SendMessage.builder()
                            .chatId(user.getChatId())
                            .parseMode(ParseMode.HTML)
                            .text(getNotificationMessage(notification))
                            .disableWebPagePreview(!notification.isHavePreview())
                            .build()
            );
        }
    }

    private void sendFileMessage(List<User> users, Notification notification, String fileId) {
        for(User user: users) {
            mainBotController.startCommandReceived(
                    SendDocument.builder()
                            .chatId(user.getChatId())
                            .parseMode(ParseMode.HTML)
                            .document(new InputFile(fileId))
                            .caption(getNotificationMessage(notification))
                            .build()
            );
        }
    }

    private void sendPhotoMessage(List<User> users, Notification notification, String fileId) {
        for(User user: users) {
            mainBotController.startCommandReceived(
                    SendPhoto.builder()
                            .chatId(user.getChatId())
                            .parseMode(ParseMode.HTML)
                            .photo(new InputFile(fileId))
                            .caption(getNotificationMessage(notification))
                            .build()
            );
        }
    }
}
