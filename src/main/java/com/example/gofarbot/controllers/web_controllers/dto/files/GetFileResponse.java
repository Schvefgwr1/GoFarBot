package com.example.gofarbot.controllers.web_controllers.dto.files;

import com.example.gofarbot.controllers.web_controllers.dto.BaseResponse;
import com.example.gofarbot.models.File;
import lombok.Getter;
import lombok.experimental.SuperBuilder;


@SuperBuilder
@Getter
public class GetFileResponse extends BaseResponse {
    private final File file;
    private final String base64FileValue;
}
