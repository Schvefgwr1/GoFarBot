package com.example.gofarbot.data;

import com.example.gofarbot.models.Admin;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AdminRepository extends CrudRepository<Admin, Long> {
    Optional<Admin> findAdminByUsername(String username);
}
