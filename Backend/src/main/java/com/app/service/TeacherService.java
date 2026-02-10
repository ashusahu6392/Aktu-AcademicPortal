package com.app.service;

import com.app.entity.Teacher;
import java.util.List;

public interface TeacherService {

    Teacher saveTeacher(Teacher teacher);

    List<Teacher> getAllTeachers();
}
