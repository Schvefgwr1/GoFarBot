package com.example.gofarbot.services.web_services;

import com.example.gofarbot.controllers.web_controllers.dto.users.GetUsersByConferenceResponse;
import com.example.gofarbot.data.UserRepository;
import com.example.gofarbot.models.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UsersService {
    private final UserRepository userRepository;

    public GetUsersByConferenceResponse getUsersByConferenceId(long conferenceId, Integer page, Integer limit) {
        if(page == null || limit == null) {
            return GetUsersByConferenceResponse.builder()
                    .code((short) 400)
                    .message("Incorrect page or limit param")
                    .build();
        }
        try {
            Page<User> usersPage = userRepository.findUsersByConferenceId(conferenceId, PageRequest.of(page, limit));
            List<User> users = usersPage.getContent();
            return GetUsersByConferenceResponse.builder()
                    .code((short) 200)
                    .message("Successful operation")
                    .users(users)
                    .limit(limit)
                    .page(page)
                    .build();
        } catch (Exception e) {
            return GetUsersByConferenceResponse.builder()
                    .code((short) 500)
                    .message("Internal server error")
                    .build();
        }
    }
}
