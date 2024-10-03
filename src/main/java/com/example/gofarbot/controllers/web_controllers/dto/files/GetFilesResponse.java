package com.example.gofarbot.controllers.web_controllers.dto.files;

import com.example.gofarbot.controllers.web_controllers.dto.BaseResponse;
import com.example.gofarbot.models.File;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
public class GetFilesResponse extends BaseResponse {
    private final List<File> files;
}
