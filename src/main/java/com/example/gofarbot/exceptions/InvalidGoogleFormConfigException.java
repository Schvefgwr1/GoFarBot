package com.example.gofarbot.exceptions;

import lombok.Getter;

@Getter
public class InvalidGoogleFormConfigException extends Exception {

    public enum Reason {
        NOT_FOUND,
        PARSE_ERROR,
        MAPPING_ERROR,
        IO_ERROR,
        SECURITY_ERROR
    }

    private final String configPath;
    private final Reason reason;

    public InvalidGoogleFormConfigException(String configPath, Reason reason, String message, Throwable cause) {
        super(message, cause);
        this.configPath = configPath;
        this.reason = reason;
    }

    @Override
    public String toString() {
        return String.format(
                "InvalidGoogleFormConfigException{reason=%s, configPath='%s', message='%s', cause=%s}",
                reason,
                configPath,
                getMessage(),
                getCause() != null ? getCause().getClass().getSimpleName() + ": " + getCause().getMessage() : "null"
        );
    }
}
