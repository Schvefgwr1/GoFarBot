package com.example.gofarbot.controllers.web_controllers.dto.files;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;


@Getter
@AllArgsConstructor
public class UploadFileRequest {
    private static final HashSet<String> types = new HashSet<>() {{
        this.add("application/pdf");
        this.add("application/png");
    }};

    @NotNull
    private String fileString;

    @NotNull
    private String fileName;

    @NotNull
    private String typeOfFile;

    public boolean isValidFile() {
        return types.contains(this.typeOfFile);
    }

    public boolean isPDF() {
        return this.typeOfFile.equals("application/pdf");
    }

    public boolean isPNG() {
        return this.typeOfFile.equals("application/png");
    }
}
