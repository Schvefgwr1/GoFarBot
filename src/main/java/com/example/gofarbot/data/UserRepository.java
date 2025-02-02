package com.example.gofarbot.data;


import com.example.gofarbot.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findUserByChatId(long chatId);

    @Modifying
    @Transactional
    @Query(value = """
        UPDATE users u SET state = :message_id
        WHERE u.chat_id = :chat_id
    """, nativeQuery = true)
    void updateUserState(
            @Param("chat_id") long chatId,
            @Param("message_id") long messageId
    );

    @Query("""
        SELECT ur.user FROM UserRegistration ur
        WHERE ur.conferenceId = :conferenceId
    """)
    Page<User> findUsersByConferenceId(@Param("conferenceId") Long conferenceId, Pageable pageable);
}