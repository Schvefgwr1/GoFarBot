package com.example.gofarbot.data;


import com.example.gofarbot.models.DialogState;
import com.example.gofarbot.models.DialogState.DialogStates;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DialogStateRepository extends CrudRepository<DialogState, Long> {
    Optional<DialogState> findDialogStateByState(DialogStates state);
}
