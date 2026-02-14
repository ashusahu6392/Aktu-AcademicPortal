package com.app.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.entity.Branch;
import com.app.entity.Course;
import com.app.entity.Subject;
import com.app.repository.BranchRepository;
import com.app.repository.CourseRepository;
import com.app.repository.SubjectRepository;
import com.app.service.AdminService;

import java.util.HashSet;
import java.util.Set;

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

        // Persist the Subject first so it becomes managed, then associate it with the existing Branch
        Subject saved = subjectRepository.save(subject);

        // Use helper to maintain both sides
        branch.addSubject(saved);

        // Save branch to update the join table
        branchRepository.save(branch);

        return saved;
    }

    @Override
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        // Remove cascading branches
        courseRepository.delete(course);
    }

    @Override
    public void deleteBranch(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new RuntimeException("Branch not found"));
        // Note: subjects remain; simply delete branch
        branchRepository.delete(branch);
    }

    @Override
    @Transactional
    public void deleteSubject(String subjectCode) {
        Subject subject = subjectRepository.findById(subjectCode)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        // Iterate over a copy of the branches set to avoid ConcurrentModification and ensure we update the owning side
        Set<Branch> branches = new HashSet<>(subject.getBranches());
        for (Branch b : branches) {
            b.getSubjects().remove(subject); // update owning side
            branchRepository.save(b); // persist the change so join table rows are removed
        }

        // Now safe to delete the subject
        subjectRepository.delete(subject);
    }
}