package com.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.entity.Branch;
import com.app.entity.Course;
import com.app.entity.Subject;
import com.app.service.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/course")
    public ResponseEntity<Course> addCourse(@RequestParam String name) {
        return ResponseEntity.ok(adminService.addCourse(name));
    }

    @PostMapping("/branch")
    public ResponseEntity<Branch> addBranch(
            @RequestParam Long courseId,
            @RequestParam String branchName) {

        return ResponseEntity.ok(
                adminService.addBranch(courseId, branchName));
    }

    @PostMapping("/subject")
    public ResponseEntity<Subject> addSubject(
            @RequestParam Long branchId,
            @RequestParam String subjectCode,
            @RequestParam String subjectName) {

        return ResponseEntity.ok(
                adminService.addSubjectToBranch(
                        branchId, subjectCode, subjectName));
    }
}
