package com.app.service.impl;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.app.entity.Teacher;
import com.app.repository.TeacherRepository;
import com.app.service.TeacherService;
import com.app.dto.TeacherRegisterDto;
import com.app.entity.Subject;
import com.app.repository.SubjectRepository;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;
    private final PasswordEncoder passwordEncoder;

    public TeacherServiceImpl(TeacherRepository teacherRepository,
                              SubjectRepository subjectRepository,
                              PasswordEncoder passwordEncoder) {
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Teacher saveTeacher(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Override
    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    @Override
    public Teacher register(TeacherRegisterDto dto) {
        Set<Subject> subjects = new HashSet<>(subjectRepository.findAllById(dto.getSubjectCodes()));

        Teacher teacher = new Teacher();
        teacher.setName(dto.getName());
        teacher.setEmail(dto.getEmail());
        teacher.setPassword(passwordEncoder.encode(dto.getPassword()));
        teacher.setSubjects(subjects);

        return teacherRepository.save(teacher);
    }
}