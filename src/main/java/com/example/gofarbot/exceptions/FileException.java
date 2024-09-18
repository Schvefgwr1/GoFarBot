package com.example.gofarbot.exceptions;

import com.example.gofarbot.models.DialogState;
import com.example.gofarbot.models.File;

public class FileException extends Exception {
    private final File.FileType type;
    private final String name;

    public FileException(String msg, String name, File.FileType type) {
        super(msg);
        this.type = type;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.getMessage() + " with file: " + this.name + " with type: " + this.type;
    }
}
