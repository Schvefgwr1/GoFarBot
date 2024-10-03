package com.example.gofarbot.security.services;


import com.example.gofarbot.data.AdminRepository;
import com.example.gofarbot.models.Admin;
import com.example.gofarbot.security.models.RefreshTokenRequest;
import com.example.gofarbot.security.models.SignInRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class AuthService {

    private final AdminRepository ourUserRepo;
    private final JWTUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public Map<String, Object> signIn(SignInRequest request){
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            var user = ourUserRepo.findAdminByUsername(request.getUsername()).orElseThrow();

            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            return Map.ofEntries(
                Map.entry("status", 200),
                Map.entry("token", jwt),
                Map.entry("refreshToken", refreshToken),
                Map.entry("expirationTime", "24 Hours"),
                Map.entry("message", "Successfully Signed In")
            );
        }catch (Exception e){
            return Map.ofEntries(
                    Map.entry("status", 400),
                    Map.entry("message", e.getMessage())
            );
        }
    }

    public Map<String, Object> refreshToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            String ourUsername = jwtUtils.extractUsername(refreshTokenRequest.getRefreshToken());
            if(Objects.equals(ourUsername, refreshTokenRequest.getUsername())) {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(ourUsername, refreshTokenRequest.getPassword()));
                Admin user = ourUserRepo.findAdminByUsername(ourUsername).orElseThrow();
                if (jwtUtils.isTokenValid(refreshTokenRequest.getRefreshToken(), user)) {
                    var jwt = jwtUtils.generateToken(user);
                    return Map.ofEntries(
                            Map.entry("status", 200),
                            Map.entry("token", jwt),
                            Map.entry("refreshToken", refreshTokenRequest.getRefreshToken()),
                            Map.entry("expirationTime", "24 Hours"),
                            Map.entry("message", "Successfully Refreshed Token")
                    );
                }
                else {
                    throw new Exception("Invalid token");
                }
            }
            else {
                throw new Exception("Invalid username");
            }
        }
        catch(Exception e) {
            return Map.ofEntries(
                    Map.entry("status", 400),
                    Map.entry("message", e.getMessage())
            );
        }
    }
}