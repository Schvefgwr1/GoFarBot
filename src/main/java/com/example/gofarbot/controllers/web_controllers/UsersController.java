package com.example.gofarbot.controllers.web_controllers;


import com.example.gofarbot.controllers.web_controllers.dto.users.GetUsersByConferenceResponse;
import com.example.gofarbot.services.web_services.UsersService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/users", produces = "application/json")
@AllArgsConstructor
@Slf4j
public class UsersController {
    private final UsersService usersService;

    @GetMapping("/conference/{id}")
    public ResponseEntity<GetUsersByConferenceResponse> getUsersByConference(
            @PathVariable("id") long conferenceId,
            @RequestParam Integer page,
            @RequestParam Integer limit
    ) {
        GetUsersByConferenceResponse response = usersService.getUsersByConferenceId(
                conferenceId,
                page,
                limit
        );
        return new ResponseEntity<>(response, HttpStatusCode.valueOf((response.getCode())));
    }
}
