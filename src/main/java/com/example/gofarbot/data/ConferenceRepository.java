package com.example.gofarbot.data;

import com.example.gofarbot.models.Conference;
import com.example.gofarbot.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface ConferenceRepository extends CrudRepository<Conference, Long> {

    @Query(value = """
        SELECT *
        FROM conferences conf
        WHERE ABS(EXTRACT(EPOCH FROM (conf.time_of_conference - :time))) < 60
        LIMIT 1
    """, nativeQuery = true)
    Optional<Conference> findConferenceByTime(@Param("time") LocalDateTime dateTime);

    @Query(value = """
        SELECT *
        FROM conferences conf
        WHERE conf.time_of_conference::date = :day
    """, nativeQuery=true)
    List<Conference> findAllByDate(@Param("day") LocalDate date);

    @Query("""
        SELECT conf
        FROM Conference conf
        WHERE conf.timeOfConference > :time
    """)
    List<Conference> findAllAfterTime(@Param("time") LocalDateTime dateTime);

    Optional<Conference> findById(long id);

    @Query(value = """
        SELECT COUNT(cur)
        FROM users u
        JOIN conferences_users_rel cur on u.id = cur.user_id
        JOIN conferences c on c.id = cur.conference
        WHERE u.chat_id = :userId
    """, nativeQuery = true)
    long findCountOfUserConferences(@Param("userId") long userId);

    @Query(value = """
        SELECT u.chat_id
        FROM users u
        JOIN conferences_users_rel ur ON u.id = ur.user_id
        WHERE ur.conference = :conferenceId
    """, nativeQuery = true)
    List<Long> findUsersOfConference(@Param("conferenceId") long conferenceId);
}
