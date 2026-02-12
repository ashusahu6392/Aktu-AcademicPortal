package com.app.service.impl;

import org.springframework.stereotype.Service;

import com.app.entity.Branch;
import com.app.entity.Course;
import com.app.entity.Subject;
import com.app.repository.BranchRepository;
import com.app.repository.CourseRepository;
import com.app.repository.SubjectRepository;
import com.app.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

    private final CourseRepository courseRepository;
    private final BranchRepository branchRepository;
    private final SubjectRepository subjectRepository;

    // Explicit constructor for DI
    public AdminServiceImpl(CourseRepository courseRepository,
                            BranchRepository branchRepository,
                            SubjectRepository subjectRepository) {
        this.courseRepository = courseRepository;
        this.branchRepository = branchRepository;
        this.subjectRepository = subjectRepository;
    }

    @Override
    public Course addCourse(String courseName) {
        Course course = Course.ofName(courseName);
        return courseRepository.save(course);
    }

    @Override
    public Branch addBranch(Long courseId, String branchName) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Branch branch = Branch.ofNameAndCourse(branchName, course);
        return branchRepository.save(branch);
    }

    @Override
    public Subject addSubjectToBranch(Long branchId,
                                      String subjectCode,
                                      String subjectName) {

        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));

        Subject subject = Subject.of(subjectCode, subjectName);

        branch.getSubjects().add(subject);
        subject.getBranches().add(branch);

        subjectRepository.save(subject);

        return subject;
    }
}