package com.example.gofarbot.exceptions;


import lombok.Getter;

@Getter
public class ConferenceException extends Exception {
    private final long conferenceId;
    public ConferenceException(String message, long conferenceId) {
        super(message);
        this.conferenceId = conferenceId;
    }
}
