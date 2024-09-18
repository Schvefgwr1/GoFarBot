package com.example.gofarbot.data;


import com.example.gofarbot.models.DialogState;
import com.example.gofarbot.models.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
    Optional<Message> findByNumberAndDialog(Integer number, DialogState dialogState);
}
