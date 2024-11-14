package com.example.gofarbot.services;


import com.example.gofarbot.controllers.bot_controllers.MainBotController;
import com.example.gofarbot.data.ConferenceRepository;
import com.example.gofarbot.data.FileRepository;
import com.example.gofarbot.models.Conference;
import com.example.gofarbot.models.File;
import com.example.gofarbot.services.notifications.DynamicNotificationService;
import com.example.gofarbot.services.web_services.FileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class StartupRunner implements ApplicationRunner {
    private final DynamicNotificationService dynamicNotificationService;
    private final ConferenceRepository conferenceRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;
    private final MainBotController mainBotController;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<Conference> conferenceList = conferenceRepository.findAllAfterTime(LocalDateTime.now());
        for(Conference conference : conferenceList) {
            dynamicNotificationService.scheduleNotificationForConference(conference.getTimeOfConference());
        }
        log.info("Successful creating of notifications to actual conferences in  database");

        List<File> files = fileRepository.findAll();
        for(File file: files) {
            if(file.getFileId() == null) {
                try {
                    String fileId;
                    Long fatherUserId = 411240604L;
                    if (file.getType() == File.FileType.DOCUMENT) {
                        Message message = mainBotController.executeDocument(SendDocument.builder()
                                .chatId(fatherUserId)
                                .document(fileService.getFile(file.getLink(), file.getType()))
                                .build()
                        );
                        fileId = message.getDocument().getFileId();
                    }
                    else {
                        Message message = mainBotController.executePhoto(SendPhoto.builder()
                                .chatId(fatherUserId)
                                .photo(fileService.getFile(file.getLink(), file.getType()))
                                .build()
                        );
                        fileId = message.getPhoto().get(0).getFileId();
                    }
                    file.setFileId(fileId);
                    File saveFile = fileRepository.save(file);
                    log.info("Successful indexing file: {}", saveFile);
                } catch (TelegramApiException e) {
                    log.error("Error of tg API in startupRunner: {}", e.getMessage());
                } catch (Exception e) {
                    log.error("Unsupported exception: {}", e.getMessage());
                }
            }
        }
    }
}