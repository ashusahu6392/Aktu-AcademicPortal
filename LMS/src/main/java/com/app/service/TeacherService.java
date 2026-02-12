package com.app.service;

import com.app.dto.TeacherRegisterDto;
import com.app.entity.Teacher;
import java.util.List;

public interface TeacherService {

    Teacher saveTeacher(Teacher teacher);

    List<Teacher> getAllTeachers();

    // New method for registration
    Teacher register(TeacherRegisterDto dto);
}