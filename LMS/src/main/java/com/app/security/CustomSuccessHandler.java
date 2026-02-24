package com.app.security;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.app.entity.Instructor;
import com.app.repository.InstructorRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    private final InstructorRepository instructorRepository;

    public CustomSuccessHandler(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
                                        throws IOException {

        var authorities = authentication.getAuthorities();
        HttpSession session = request.getSession();

        // Look up the instructor record by email to get the id and role stored in DB
        String email = authentication.getName();
        Optional<Instructor> opt = instructorRepository.findByEmail(email);
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            // mark admin session so AdminController's simple session check works
            session.setAttribute("isAdmin", true);
            opt.ifPresent(i -> session.setAttribute("adminId", i.getId()));
            // also store email
            session.setAttribute("adminEmail", email);
            response.sendRedirect("/admin");
            return;
        }

        // Non-admin: set instructor session id so existing controller flows work
        session.setAttribute("instructorEmail", email);
        opt.ifPresent(i -> session.setAttribute("instructorId", i.getId()));

        response.sendRedirect("/instructor/dashboard");
    }
}