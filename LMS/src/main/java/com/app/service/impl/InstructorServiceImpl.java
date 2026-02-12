package com.app.service.impl;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.app.entity.Instructor;
import com.app.repository.InstructorRepository;
import com.app.service.InstructorService;
import com.app.dto.InstructorRegisterDto;
import com.app.entity.Subject;
import com.app.repository.SubjectRepository;

@Service
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final SubjectRepository subjectRepository;
    private final PasswordEncoder passwordEncoder;

    public InstructorServiceImpl(InstructorRepository instructorRepository,
                                 SubjectRepository subjectRepository,
                                 PasswordEncoder passwordEncoder) {
        this.instructorRepository = instructorRepository;
        this.subjectRepository = subjectRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Instructor saveInstructor(Instructor instructor) {
        return instructorRepository.save(instructor);
    }

    @Override
    public List<Instructor> getAllInstructors() {
        return instructorRepository.findAll();
    }

    @Override
    public Instructor register(InstructorRegisterDto dto) {
        Set<Subject> subjects = new HashSet<>(subjectRepository.findAllById(dto.getSubjectCodes()));

        Instructor instructor = new Instructor();
        instructor.setName(dto.getName());
        instructor.setEmail(dto.getEmail());
        instructor.setPassword(passwordEncoder.encode(dto.getPassword()));
        instructor.setSubjects(subjects);

        return instructorRepository.save(instructor);
    }
}