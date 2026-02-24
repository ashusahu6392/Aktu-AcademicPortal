package com.app.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.entity.Instructor;
import com.app.repository.InstructorRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final InstructorRepository instructorRepository;

    public CustomUserDetailsService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Instructor instructor = instructorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Stored role in DB may already include the "ROLE_" prefix (e.g. "ROLE_ADMIN").
        // User.builder().roles(...) will automatically add "ROLE_" so ensure we pass
        // the role name without that prefix. Also handle null/empty role values.
        String storedRole = instructor.getRole();
        String roleToUse;
        if (storedRole == null || storedRole.trim().isEmpty()) {
            roleToUse = "INSTRUCTOR"; // sensible default
        } else if (storedRole.startsWith("ROLE_")) {
            roleToUse = storedRole.substring("ROLE_".length());
        } else {
            roleToUse = storedRole;
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(instructor.getEmail())
                .password(instructor.getPassword())
                .roles(roleToUse) // pass role without "ROLE_" prefix
                .build();
    }
}