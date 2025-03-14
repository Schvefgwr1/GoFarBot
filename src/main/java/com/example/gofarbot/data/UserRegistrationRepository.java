package com.example.gofarbot.data;

import com.example.gofarbot.models.UserRegistration;
import org.springframework.data.repository.CrudRepository;

public interface UserRegistrationRepository extends CrudRepository<UserRegistration, Long> {
}
