package com.lastversion.user.security_and_jwt.service;

import com.lastversion.common.entity.AdminEntity;
import com.lastversion.common.entity.UserEntity;
import com.lastversion.user.repository.AdminRepository;
import com.lastversion.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }

    public UserDetails loadUserByUsernameAndPassword(String email, String password) throws UsernameNotFoundException {
        AdminEntity admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with email: " + email));
        return new User(admin.getEmail(), admin.getPassword(), new ArrayList<>());
    }

}

