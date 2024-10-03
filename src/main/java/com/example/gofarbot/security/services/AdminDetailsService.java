package com.example.gofarbot.security.services;


import com.example.gofarbot.data.AdminRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdminDetailsService implements UserDetailsService {
    private AdminRepository ourUserRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return ourUserRepo.findAdminByUsername(username).orElseThrow();
    }
}