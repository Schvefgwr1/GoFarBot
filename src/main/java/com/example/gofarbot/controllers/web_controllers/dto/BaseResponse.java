package com.example.gofarbot.controllers.web_controllers.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Getter
@SuperBuilder
public class BaseResponse {
    private final short code;
    private final String message;
}
