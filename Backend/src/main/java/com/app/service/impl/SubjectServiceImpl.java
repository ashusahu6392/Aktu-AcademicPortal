package com.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.app.entity.Subject;
import com.app.repository.SubjectRepository;
import com.app.service.SubjectService;

@Service
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectServiceImpl(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Override
    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    @Override
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }
}
