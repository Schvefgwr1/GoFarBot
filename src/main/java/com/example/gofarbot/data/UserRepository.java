package com.example.gofarbot.data;


import com.example.gofarbot.models.DialogState;
import com.example.gofarbot.models.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findUserByUserId(long userId);

    Optional<User> findUserByChatId(long chatId);

    @Modifying
    @Transactional
    @Query(value = """
    UPDATE users u SET state = (
        SELECT msg.id
        FROM messages msg
            JOIN dialog_states ds on msg.dialog = ds.id
        WHERE msg.number = :number AND
            ds.state = :dialog_state_e
        LIMIT 1
    )
    WHERE u.chat_id = :chat_id
    """, nativeQuery = true)
    void updateUserState(
            @Param("chat_id") long chatId,
            @Param("dialog_state_e") String dialogStateE,
            @Param("number") int number
    );
}
