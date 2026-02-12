package com.app.service;

import com.app.entity.Branch;
import com.app.entity.Course;
import com.app.entity.Subject;

public interface AdminService {

    Course addCourse(String courseName);

    Branch addBranch(Long courseId, String branchName);

    Subject addSubjectToBranch(Long branchId,
                               String subjectCode,
                               String subjectName);

    // Deletion APIs
    void deleteCourse(Long courseId);

    void deleteBranch(Long branchId);

    void deleteSubject(String subjectCode);
}