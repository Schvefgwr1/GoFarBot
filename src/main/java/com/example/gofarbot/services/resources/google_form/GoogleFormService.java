package com.example.gofarbot.services.resources.google_form;

import com.example.gofarbot.exceptions.InvalidGoogleFormConfigException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.ServiceAccountCredentials;
import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;

@Slf4j
public class GoogleFormService {

    private final Sheets sheetsService;
    private final GoogleFormConfig config;

    public GoogleFormService(String configPath) throws InvalidGoogleFormConfigException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream configStream = getClass().getClassLoader().getResourceAsStream(configPath);

        if (configStream == null) {
            throw new InvalidGoogleFormConfigException(
                    configPath,
                    InvalidGoogleFormConfigException.Reason.NOT_FOUND,
                    "Config file not found at path: " + configPath,
                    null
            );
        }

        try {
            this.config = mapper.readValue(configStream, GoogleFormConfig.class);
        } catch (com.fasterxml.jackson.core.JsonParseException e) {
            throw new InvalidGoogleFormConfigException(
                    configPath,
                    InvalidGoogleFormConfigException.Reason.PARSE_ERROR,
                    "Malformed JSON in config: " + configPath,
                    e
            );
        } catch (com.fasterxml.jackson.databind.JsonMappingException e) {
            throw new InvalidGoogleFormConfigException(
                    configPath,
                    InvalidGoogleFormConfigException.Reason.MAPPING_ERROR,
                    "JSON does not match expected structure: " + configPath,
                    e
            );
        } catch (IOException e) {
            throw new InvalidGoogleFormConfigException(
                    configPath,
                    InvalidGoogleFormConfigException.Reason.IO_ERROR,
                    "I/O error while reading config: " + configPath,
                    e
            );
        }

        try {
            ServiceAccountCredentials credentials = ServiceAccountCredentials
                    .fromStream(new FileInputStream(config.getCredentialsPath()));
            
            this.sheetsService = new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials)
            )
            .setApplicationName("GoFarBot")
            .build();
        } catch (IOException e) {
            throw new InvalidGoogleFormConfigException(
                    configPath,
                    InvalidGoogleFormConfigException.Reason.IO_ERROR,
                    "Failed to load Google credentials from path: " + config.getCredentialsPath(),
                    e
            );
        } catch (GeneralSecurityException e) {
            throw new InvalidGoogleFormConfigException(
                    configPath,
                    InvalidGoogleFormConfigException.Reason.SECURITY_ERROR,
                    "Failed to load Google Service with security troubles: " + config.getCredentialsPath(),
                    e
            );
        }
    }

    public boolean notUserExistsByTelegramUsername(String username) throws IOException {
        ValueRange valueRange = sheetsService.spreadsheets().values()
                .get(config.getSpreadsheetId(), config.getSheetName())
                .execute();

        List<List<Object>> rows = valueRange.getValues();
        if (rows == null || rows.isEmpty()) {
            return false;
        }

        int nicknameCol = -1;
        List<Object> header = rows.get(0);

        for (int i = 0; i < header.size(); i++) {
            if (config.getColumnName().equalsIgnoreCase(header.get(i).toString().trim())) {
                nicknameCol = i;
                break;
            }
        }

        if (nicknameCol == -1) {
            throw new IOException("Column '" + config.getColumnName() + "' not found in Google Sheet: " + config.getSheetName());
        }

        boolean isUserExists = false;
        for (int i = 1; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            if (nicknameCol < row.size()) {
                String cell = row.get(nicknameCol).toString().trim();
                if (Objects.equals(cell, username) || Objects.equals(cell.replace("@", ""), username)) {
                    isUserExists = true;
                }
            }
        }

        return !isUserExists;
    }

}
