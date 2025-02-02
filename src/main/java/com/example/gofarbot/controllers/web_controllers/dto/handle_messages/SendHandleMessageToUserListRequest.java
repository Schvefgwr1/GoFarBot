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
public class SendHandleMessageToUserListRequest {
    @NotNull
    private final String message;

    @NotEmpty
    private final List<Long> usersIds;

    @Null
    private final UploadFileRequest uploadFileRequest;
}
