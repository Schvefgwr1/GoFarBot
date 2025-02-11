package com.example.gofarbot.services.web_services;

import com.example.gofarbot.data.NotificationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationWebService {
    private final NotificationRepository notificationRepository;

}
