package com.example.gofarbot.exceptions;

import com.example.gofarbot.models.DialogState;
import lombok.Getter;

import java.util.function.Supplier;

public class MessageException extends Exception {

    public MessageException(String msg) {
        super(msg);
    }

}
