package com.example.gofarbot.exceptions;

import com.example.gofarbot.models.DialogState;
import lombok.Getter;

@Getter
public class DialogStateException extends Exception {
    private final DialogState.DialogStates dialogState;

    public DialogStateException(String msg, DialogState.DialogStates dialogState) {
        super(msg);
        this.dialogState = dialogState;
    }

    @Override
    public String toString() {
        return this.getMessage() + " " + this.getDialogState();
    }
}
