package com.example.gofarbot.services.notifications;


import com.example.gofarbot.controllers.bot_controllers.MainBotController;
import com.example.gofarbot.data.FileRepository;
import com.example.gofarbot.data.UserRepository;
import com.example.gofarbot.models.*;
import com.example.gofarbot.services.bot_services.KeyboardsService;
import com.example.gofarbot.services.web_services.FileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class MessageNotificationService {
    private final Long fatherUserId = 411240604L;
    private final MainBotController mainBotController;
    private final FileService fileService;
    private final KeyboardsService keyboardsService;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    @Scheduled(cron = "0 0 18 7 10 *")
    public void scheduledTask() {
        if (LocalDateTime.now().getYear() == 2024) {
            Iterable<User> users = userRepository.findAll();
            users.forEach(user ->
                mainBotController.startCommandReceived(SendMessage.builder()
                        .chatId(user.getChatId())
                        .text("""
                        Пропустил вебинар? Мы ценим твою заинтересованность!
                        Повтор видео-встречи можешь посмотреть по закрытой ссылке на нашем YouTube-канале
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
                            .parseMode(ParseMode.HTML)
                            .text(getNotificationMessage(notification))
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
