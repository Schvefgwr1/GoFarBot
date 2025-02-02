package com.example.gofarbot.services.web_services;


import com.example.gofarbot.controllers.web_controllers.dto.conferences.CreateConferenceRequest;
import com.example.gofarbot.controllers.web_controllers.dto.conferences.CreateConferenceResponse;
import com.example.gofarbot.controllers.web_controllers.dto.conferences.GetConferenceResponse;
import com.example.gofarbot.controllers.web_controllers.dto.conferences.GetConferencesResponse;
import com.example.gofarbot.data.ConferenceRepository;
import com.example.gofarbot.exceptions.ConferenceException;
import com.example.gofarbot.exceptions.DatabaseException;
import com.example.gofarbot.models.Conference;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@AllArgsConstructor
@Slf4j
public class ConferenceService {
    private final ConferenceRepository conferenceRepository;

    public GetConferencesResponse getConferences() {
        try {
            Iterable<Conference> iterable = conferenceRepository.findAll();
            ArrayList<Conference> conferences = new ArrayList<>();
            iterable.forEach((conference) -> {
                conference.setRegistrations(new ArrayList<>());
                conference.setNotifications(new ArrayList<>());
                conferences.add(conference);
            });
            return GetConferencesResponse.builder()
                    .conferences(conferences)
                    .code((short) 200)
                    .message("Successful operation")
                    .build();
        }
        catch (Exception e) {
            log.error(new DatabaseException(
                    "Error of database",
                    ConferenceRepository.class.getName(),
                    "getConferences"
            ).toString());
            return GetConferencesResponse.builder()
                    .code((short) 400)
                    .message("Error of operation")
                    .build();
        }

    }

    public GetConferenceResponse getConference(long conferenceId) {
        try {
            Conference conference = conferenceRepository.findById(conferenceId)
                    .orElseThrow(() -> new ConferenceException("Can't find conference", conferenceId));
            return GetConferenceResponse.builder()
                    .conference(conference)
                    .code((short) 200)
                    .message("Successful operation")
                    .build();
        }
        catch (ConferenceException e) {
            log.error(e.toString());
            return GetConferenceResponse.builder()
                    .code((short) 400)
                    .message(e.getMessage())
                    .build();
        }
        catch (Exception e) {
            log.error(new DatabaseException(
                    "Error of database",
                    ConferenceRepository.class.getName(),
                    "getConferences"
            ).toString());
            return GetConferenceResponse.builder()
                    .code((short) 400)
                    .message("Error of operation")
                    .build();
        }

    }

    public CreateConferenceResponse createConference(CreateConferenceRequest request) {
        try {
            Conference conference = Conference.builder()
                    .link(request.getLink())
                    .name(request.getName())
                    .timeOfConference(request.getTime())
                    .build();
            Conference saveConference = conferenceRepository.save(conference);
            return CreateConferenceResponse.builder()
                    .code((short) 200)
                    .message("Successful creating")
                    .conference(saveConference)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return CreateConferenceResponse.builder()
                    .code((short) 400)
                    .message("Error of database operation")
                    .build();
        }
    }
}
