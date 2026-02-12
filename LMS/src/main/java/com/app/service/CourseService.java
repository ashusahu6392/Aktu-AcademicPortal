package com.app.service;

import com.app.entity.Course;
import java.util.List;

public interface CourseService {

    Course saveCourse(Course course);

    List<Course> getAllCourses();
}
