package com.app.controller;

import com.app.entity.Instructor;
import com.app.repository.InstructorRepository;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice(basePackages = "com.app.controller")
public class InstructorModelAdvice {

    private final InstructorRepository instructorRepository;

    public InstructorModelAdvice(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    @ModelAttribute("instructorProfile")
    public Instructor populateInstructorProfile(HttpSession session) {
        Object iid = session.getAttribute("instructorId");
        if (iid == null) return null;

        // Support Long, Integer or String session values (flexible)
        try {
            Long id;
            if (iid instanceof Long) {
                id = (Long) iid;
            } else if (iid instanceof Integer) {
                id = ((Integer) iid).longValue();
            } else {
                id = Long.valueOf(String.valueOf(iid));
            }
            return instructorRepository.findById(id).orElse(null);
        } catch (Exception ex) {
            return null;
        }
    }
}
