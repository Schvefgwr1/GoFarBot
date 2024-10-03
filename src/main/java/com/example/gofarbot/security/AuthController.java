package com.example.gofarbot.security;

import com.example.gofarbot.security.models.RefreshTokenRequest;
import com.example.gofarbot.security.models.SignInRequest;
import com.example.gofarbot.security.services.AuthService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/elrt65jl3r5t43le78rjkt43ew34rw/auth")
@AllArgsConstructor
@Slf4j
public class AuthController {

    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody SignInRequest signInRequest){
        Map<String, Object> response = authService.signIn(signInRequest);
        HttpStatus httpStatus;
        if((int) response.get("status") == 200) {
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest){
        Map<String, Object> response = authService.refreshToken(refreshTokenRequest);
        HttpStatus httpStatus;
        if((int) response.get("status") == 200) {
            httpStatus = HttpStatus.OK;
        } else {
            httpStatus = HttpStatus.BAD_REQUEST;
        }
        return new ResponseEntity<>(response, httpStatus);
    }
}