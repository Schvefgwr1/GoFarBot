package com.example.gofarbot.data;


import com.example.gofarbot.models.Stat;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatRepository extends CrudRepository<Stat, Long> {
    Optional<Stat> findByCode(String code);
}
