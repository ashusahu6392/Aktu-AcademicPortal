package com.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.entity.Subject;
import com.app.entity.Instructor;
import com.app.dto.InstructorRegisterDto;
import com.app.repository.SubjectRepository;
import com.app.service.InstructorService;

@RestController
@RequestMapping("/api/instructor")
public class InstructorController {

    private final InstructorService instructorService;
    private final SubjectRepository subjectRepository;

    public InstructorController(InstructorService instructorService, SubjectRepository subjectRepository) {
        this.instructorService = instructorService;
        this.subjectRepository = subjectRepository;
    }

    // Get all subjects for dropdown (API)
    @GetMapping("/subjects")
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    // Register instructor (API)
    @PostMapping("/register")
    public ResponseEntity<Instructor> register(@RequestBody InstructorRegisterDto dto) {
        return ResponseEntity.ok(instructorService.register(dto));
    }
}