package com.example.gofarbot.services.resources.google_form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GoogleFormConfig {
    private String spreadsheetId;
    private String sheetName;
    private String credentialsPath;
    private String columnName;
}

