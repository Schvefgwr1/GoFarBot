package com.example.gofarbot.exceptions;

public class BackMessageException extends Exception {
    private final long messageId;
    private final String codeOfMessage;
    private final long sizeOfBack;

    public BackMessageException(
            String message,
            long messageId,
            String codeOfMessage,
            long sizeOfBack
    ) {
        super(message);
        this.messageId = messageId;
        this.codeOfMessage = codeOfMessage;
        this.sizeOfBack = sizeOfBack;
    }

    @Override
    public String toString() {
        return super.getMessage() + messageId + " with code of message: " + codeOfMessage +
                ". And find this count of back messages: " + sizeOfBack;
    }
}
