package com.app.service;

import com.app.entity.Subject;
import java.util.List;

public interface SubjectService {

    Subject saveSubject(Subject subject);

    List<Subject> getAllSubjects();
}
