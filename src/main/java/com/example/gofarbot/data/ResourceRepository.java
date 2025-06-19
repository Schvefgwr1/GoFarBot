package com.example.gofarbot.data;

import com.example.gofarbot.models.Resource;
import org.springframework.data.repository.CrudRepository;

public interface ResourceRepository extends CrudRepository<Resource, Long> {
}
