package com.example.gofarbot.services.web_services;


import com.example.gofarbot.controllers.bot_controllers.MainBotController;
import com.example.gofarbot.controllers.web_controllers.dto.files.UploadFileRequest;
import com.example.gofarbot.controllers.web_controllers.dto.files.UploadFileResponse;
import com.example.gofarbot.controllers.web_controllers.dto.handle_messages.SendHandleMessageToAllUsersRequest;
import com.example.gofarbot.controllers.web_controllers.dto.handle_messages.SendHandleMessageToRegUsersRequest;
import com.example.gofarbot.controllers.web_controllers.dto.handle_messages.SendHandleMessageToUsersResponse;
import com.example.gofarbot.data.*;
import com.example.gofarbot.exceptions.DialogStateException;
import com.example.gofarbot.models.DialogState;
import com.example.gofarbot.models.File;
import com.example.gofarbot.models.Message;
import com.example.gofarbot.models.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.util.HashSet;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class HandleMessageService {
    private final Long fatherUserId = 411240604L;
    private final MessageRepository messageRepository;
    private final ConferenceRepository conferenceRepository;
    private final UserRepository userRepository;
    private final MainBotController mainBotController;
    private final DialogStateRepository dialogStateRepository;
    private final FileService fileService;
    private final FileRepository fileRepository;

    public SendHandleMessageToUsersResponse sendToRegUsers(@NotNull SendHandleMessageToRegUsersRequest request) {
        HashSet<Long> chatIds = new HashSet<>();
        for(Long conferenceId: request.getConferenceIds()) {
            List<User> users = conferenceRepository.findUsersOfConference(conferenceId);
            for(User user: users) {
                chatIds.add(user.getChatId());
            }
        }
        return sendMessage(chatIds, request.getUploadFileRequest(), request.getMessage());
    }

    public SendHandleMessageToUsersResponse sendToAllUsers(@NotNull SendHandleMessageToAllUsersRequest request) {
        HashSet<Long> chatIds = new HashSet<>();
        userRepository.findAll().forEach(user ->
            chatIds.add(user.getChatId())
        );
        return sendMessage(chatIds, request.getUploadFileRequest(), request.getMessage());
    }

    private SendHandleMessageToUsersResponse sendMessage(
            HashSet<Long> chatIds,
            UploadFileRequest uploadFileRequest,
            String message
    ) {
        if(!chatIds.isEmpty()) {
            if(uploadFileRequest != null) {
                UploadFileResponse uploadFileResponse = fileService.uploadFile(uploadFileRequest);
                if (uploadFileResponse.getCode() == (short) 200) {
                    String fileId;
                    try {
                        messageRepository.save(Message.builder()
                                .text(message)
                                .number(-1)
                                .dialog(dialogStateRepository
                                        .findDialogStateByState(DialogState.DialogStates.HANDLE)
                                        .orElseThrow(() -> new DialogStateException(
                                                "No dialogState in DB",
                                                DialogState.DialogStates.HANDLE
                                        ))
                                )
                                .file(uploadFileResponse.getFile())
                                .build()
                        );
                        if (uploadFileResponse.getFile().getType() == File.FileType.DOCUMENT) {
                            fileId = mainBotController.executeDocument(SendDocument.builder()
                                    .chatId(fatherUserId)
                                    .document(fileService.getFile(
                                            uploadFileResponse.getFile().getLink(),
                                            File.FileType.DOCUMENT)
                                    )
                                    .build()
                            ).getDocument().getFileId();
                        } else {
                            fileId = mainBotController.executePhoto(SendPhoto.builder()
                                    .chatId(fatherUserId)
                                    .photo(fileService.getFile(
                                            uploadFileResponse.getFile().getLink(),
                                            File.FileType.PHOTO)
                                    )
                                    .build()
                            ).getPhoto().get(0).getFileId();
                        }
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        fileId = null;
                    }
                    if(fileId != null) {
                        uploadFileResponse.getFile().setFileId(fileId);
                        File saveFile = fileRepository.save(uploadFileResponse.getFile());
                        log.info("Successful save of file: {}", saveFile);
                        for (long chatId : chatIds) {
                            if (uploadFileResponse.getFile().getType() == File.FileType.DOCUMENT) {
                                mainBotController.startCommandReceived(SendDocument.builder()
                                        .chatId(chatId)
                                        .parseMode(ParseMode.HTML)
                                        .document(new InputFile(fileId))
                                        .caption(message)
                                        .build()
                                );
                            } else {
                                mainBotController.startCommandReceived(SendPhoto.builder()
                                        .chatId(chatId)
                                        .parseMode(ParseMode.HTML)
                                        .photo(new InputFile(fileId))
                                        .caption(message)
                                        .build()
                                );
                            }
                        }
                    }
                    else {
                        log.warn("Null fileId of File: {}", uploadFileResponse.getFile());
                    }
                } else {
                    log.warn("Error of upload file in handle message: {}", uploadFileRequest.getFileName());
                    for (long chatId : chatIds) {
                        SendTextMessageToUsers(chatId, message);
                    }
                }
            } else {
                for (long chatId : chatIds) {
                    SendTextMessageToUsers(chatId, message);
                }
                try {
                    messageRepository.save(Message.builder()
                            .text(message)
                            .number(-1)
                            .dialog(dialogStateRepository
                                    .findDialogStateByState(DialogState.DialogStates.HANDLE)
                                    .orElseThrow(() -> new DialogStateException(
                                            "No dialogState in DB",
                                            DialogState.DialogStates.HANDLE
                                    ))
                            )
                            .build()
                    );
                } catch (Exception e) {
                    log.error(e.toString());
                }
            }
            return SendHandleMessageToUsersResponse.builder()
                    .code((short) 200)
                    .message("Successful operation")
                    .build();
        }
        else {
            log.error("Incorrect find id's of users");
            return SendHandleMessageToUsersResponse.builder()
                    .code((short) 400)
                    .message("Incorrect input data")
                    .build();
        }
    }

    private void SendTextMessageToUsers(long chatId, String message) {
        mainBotController.startCommandReceived(SendMessage.builder()
                .chatId(chatId)
                .parseMode(ParseMode.HTML)
                .text(message)
                .build()
        );
    }
}
