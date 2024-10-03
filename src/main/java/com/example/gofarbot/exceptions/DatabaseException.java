package com.example.gofarbot.exceptions;


import lombok.Getter;

@Getter
public class DatabaseException extends Exception
{
    private final String repositoryName;
    private final String methodName;
    public DatabaseException(String message, String repositoryName, String methodName) {
        super(message);
        this.repositoryName = repositoryName;
        this.methodName = methodName;
    }
}
