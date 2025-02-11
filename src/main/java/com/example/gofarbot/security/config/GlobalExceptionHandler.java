package com.example.gofarbot.security.config;

import com.example.gofarbot.controllers.web_controllers.dto.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Обрабатываем ошибки 400 (Bad Request)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse> handleBadRequest(Exception ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse((short) 400, ex.getMessage()));
    }

    // Обрабатываем ошибки 403 (Forbidden)
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<BaseResponse> handleForbidden(Exception ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new BaseResponse((short) 403, "Access Denied: " + ex.getMessage()));
    }

    // Обрабатываем ошибки 500 (Internal Server Error)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> handleGlobalException(Exception ex, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse((short) 500, "Internal Server Error: " + ex.getMessage()));
    }
}
