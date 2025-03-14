package com.example.gofarbot.data;


import com.example.gofarbot.models.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {

    Optional<Message> findByCode(String code);

    @Query("""
        SELECT m
        FROM Message m
        JOIN m.buttons b
        WHERE b.code = :actualMessageCode
    """)
    List<Message> findBackMessages(@Param("actualMessageCode") String actualMessageCode);

    @Query(value = """
        SELECT *
        FROM messages m
        WHERE m.next_message = :next_message_id
        LIMIT 1
    """, nativeQuery = true)
    Optional<Message> findPreviousMessageInChain(@Param("next_message_id") long messageId);

    List<Message> findMessagesByLinkName(String linkName);
}
