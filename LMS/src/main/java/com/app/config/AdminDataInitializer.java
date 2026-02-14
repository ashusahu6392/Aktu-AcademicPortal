package com.app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.app.entity.Instructor;
import com.app.repository.InstructorRepository;

@Configuration
public class AdminDataInitializer {

    @Bean
    public CommandLineRunner seedAdmin(InstructorRepository instructorRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // create an admin instructor if none exists
            String adminEmail = "admin@example.com";
            if (instructorRepository.findByEmail(adminEmail).isEmpty()) {
                Instructor admin = new Instructor();
                admin.setName("Site Admin");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRole("ROLE_ADMIN");
                instructorRepository.save(admin);
                System.out.println("[AdminDataInitializer] Created default admin: " + adminEmail + " (password: admin123)");
            }
        };
    }
}
