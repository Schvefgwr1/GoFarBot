package com.example.gofarbot.services.web_services;


import com.example.gofarbot.controllers.web_controllers.dto.conferences.*;
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

    public UpdateConferenceResponse updateConference(UpdateConferenceRequest request, long id) {
        try {
            Conference conference = conferenceRepository.findById(id)
                    .orElseThrow(() -> new ConferenceException("Can't find conference", id));
            if(request.getName() != null) {
                conference.setName(request.getName());
            }
            if(request.getLink() != null) {
                conference.setLink(conference.getLink());
            }
            if(request.getTimeOfConference() != null) {
                conference.setTimeOfConference(request.getTimeOfConference());
            }
            conference = conferenceRepository.save(conference);
            return UpdateConferenceResponse.builder()
                    .id(conference.getId())
                    .name(conference.getName())
                    .link(conference.getLink())
                    .timeOfConference(conference.getTimeOfConference())
                    .message("Successful operation")
                    .code((short) 200)
                    .build();
        } catch (ConferenceException e) {
            return UpdateConferenceResponse.builder()
                    .message(e.getMessage())
                    .code((short) 404)
                    .build();
        } catch (Exception e) {
            return UpdateConferenceResponse.builder()
                    .message(e.getMessage())
                    .code((short) 400)
                    .build();
        }
    }
}
