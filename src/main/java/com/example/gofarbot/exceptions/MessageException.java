package com.example.gofarbot.exceptions;

import com.example.gofarbot.models.DialogState;
import lombok.Getter;

@Getter
public class MessageException extends Exception {
    private final Integer numberMsg;
    private final DialogState dialogState;

    public MessageException(String msg, Integer numberMsg, DialogState dialogState) {
        super(msg);
        this.numberMsg = numberMsg;
        this.dialogState = dialogState;
    }

    @Override
    public String toString() {
        return this.getMessage() + " " + this.getDialogState() + " " + this.getNumberMsg();
    }
}
