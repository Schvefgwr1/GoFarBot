package com.example.gofarbot.data;

import com.example.gofarbot.models.File;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends CrudRepository<File, Long> {
    @NotNull
    List<File> findAll();

    Optional<File> findByLink(String link);
}
