package com.example.gofarbot.controllers.web_controllers.dto.handle_messages;


import com.example.gofarbot.controllers.web_controllers.dto.files.UploadFileRequest;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SendHandleMessageToAllUsersRequest {
    @NotNull
    private final String message;

    @Null
    private final UploadFileRequest uploadFileRequest;
}
