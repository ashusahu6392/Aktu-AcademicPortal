package com.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.entity.Subject;
import com.app.entity.Teacher;
import com.app.dto.TeacherRegisterDto;
import com.app.repository.SubjectRepository;
import com.app.service.TeacherService;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final TeacherService teacherService;
    private final SubjectRepository subjectRepository;

    public TeacherController(TeacherService teacherService, SubjectRepository subjectRepository) {
        this.teacherService = teacherService;
        this.subjectRepository = subjectRepository;
    }

    // Get all subjects for dropdown (API)
    @GetMapping("/subjects")
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    // Register teacher (API)
    @PostMapping("/register")
    public ResponseEntity<Teacher> register(@RequestBody TeacherRegisterDto dto) {
        return ResponseEntity.ok(teacherService.register(dto));
    }
}