package com.app.service;

import com.app.dto.InstructorRegisterDto;
import com.app.entity.Instructor;
import java.util.List;

public interface InstructorService {

    Instructor saveInstructor(Instructor instructor);

    List<Instructor> getAllInstructors();

    // registration using InstructorRegisterDto
    Instructor register(InstructorRegisterDto dto);
}