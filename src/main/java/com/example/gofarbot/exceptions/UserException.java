package com.example.gofarbot.exceptions;

public class UserException extends RuntimeException {
    private final long chatId;

    public UserException(String message, long chatId) {
        super(message);
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return super.toString() + chatId;
    }
}
