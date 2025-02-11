package com.example.gofarbot.controllers.web_controllers;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/notifications", produces = "application/json")
@AllArgsConstructor
@Slf4j
public class NotificationsController {

}
