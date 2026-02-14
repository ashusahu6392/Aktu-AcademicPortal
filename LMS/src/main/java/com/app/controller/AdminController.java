package com.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.dto.BranchDto;
import com.app.dto.CourseDto;
import com.app.dto.SubjectDto;
import com.app.repository.BranchRepository;
import com.app.repository.CourseRepository;
import com.app.repository.SubjectRepository;
import com.app.service.AdminService;
import com.app.repository.InstructorRepository;
import com.app.entity.Instructor;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final CourseRepository courseRepository;
    private final BranchRepository branchRepository;
    private final SubjectRepository subjectRepository;
    private final InstructorRepository instructorRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(AdminService adminService,
                           CourseRepository courseRepository,
                           BranchRepository branchRepository,
                           SubjectRepository subjectRepository,
                           InstructorRepository instructorRepository,
                           PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.courseRepository = courseRepository;
        this.branchRepository = branchRepository;
        this.subjectRepository = subjectRepository;
        this.instructorRepository = instructorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Simple admin session check placeholder
    private boolean isAdminLoggedIn(HttpSession session) {
        // Placeholder: check session attribute "isAdmin" (set to true on a real login flow)
        Object v = session.getAttribute("isAdmin");
        return v != null && Boolean.TRUE.equals(v);
    }

    @GetMapping
    public String dashboard(Model model, HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login"; // redirect to admin login
        }

        long totalCourses = courseRepository.count();
        long totalBranches = branchRepository.count();
        long totalSubjects = subjectRepository.count();

        model.addAttribute("totalCourses", totalCourses);
        model.addAttribute("totalBranches", totalBranches);
        model.addAttribute("totalSubjects", totalSubjects);

        return "admin/dashboard";
    }

    @GetMapping("/courses")
    public String manageCourses(Model model, HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("courseDto", new CourseDto());
        return "admin/courses";
    }

    @GetMapping("/branches")
    public String manageBranches(Model model, HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        model.addAttribute("branches", branchRepository.findAll());
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("branchDto", new BranchDto());
        return "admin/branches";
    }

    @GetMapping("/subjects")
    public String manageSubjects(Model model, HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("branches", branchRepository.findAll());
        model.addAttribute("subjectDto", new SubjectDto());
        return "admin/subjects";
    }

    @PostMapping("/course")
    public String addCourse(@Valid @ModelAttribute("courseDto") CourseDto dto,
                            BindingResult bindingResult,
                            Model model,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid course data");
            return "redirect:/admin/courses";
        }

        adminService.addCourse(dto.getCourseName());
        redirectAttributes.addFlashAttribute("success", "Course created");
        return "redirect:/admin/courses";
    }

    @PostMapping("/branch")
    public String addBranch(@Valid @ModelAttribute("branchDto") BranchDto dto,
                            BindingResult bindingResult,
                            Model model,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid branch data");
            return "redirect:/admin/branches";
        }

        adminService.addBranch(dto.getCourseId(), dto.getBranchName());
        redirectAttributes.addFlashAttribute("success", "Branch created");
        return "redirect:/admin/branches";
    }

    @PostMapping("/subject")
    public String addSubject(@Valid @ModelAttribute("subjectDto") SubjectDto dto,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Invalid subject data");
            return "redirect:/admin/subjects";
        }

        adminService.addSubjectToBranch(dto.getBranchId(), dto.getSubjectCode(), dto.getSubjectName());
        redirectAttributes.addFlashAttribute("success", "Subject created");
        return "redirect:/admin/subjects";
    }

    @PostMapping("/course/delete")
    public String deleteCourse(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        adminService.deleteCourse(id);
        redirectAttributes.addFlashAttribute("success", "Course deleted");
        return "redirect:/admin/courses";
    }

    @PostMapping("/branch/delete")
    public String deleteBranch(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        adminService.deleteBranch(id);
        redirectAttributes.addFlashAttribute("success", "Branch deleted");
        return "redirect:/admin/branches";
    }

    @PostMapping("/subject/delete")
    public String deleteSubject(@RequestParam String code, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        adminService.deleteSubject(code);
        redirectAttributes.addFlashAttribute("success", "Subject deleted");
        return "redirect:/admin/subjects";
    }

    // Admin login page
    @GetMapping("/login")
    public String showLogin(Model model) {
        model.addAttribute("email", "");
        return "admin/admin-login";
    }

    // Admin login POST - authenticate against Instructor with role ROLE_ADMIN
    @PostMapping("/login")
    public String login(String email, String password, Model model, HttpSession session) {
        if (email == null || password == null) {
            model.addAttribute("error", "Email and password are required");
            return "admin/admin-login";
        }

        var opt = instructorRepository.findByEmail(email);
        if (opt.isEmpty()) {
            model.addAttribute("error", "Invalid credentials");
            return "admin/admin-login";
        }

        Instructor instructor = opt.get();
        if (!"ROLE_ADMIN".equals(instructor.getRole())) {
            model.addAttribute("error", "Not an admin");
            return "admin/admin-login";
        }

        if (!passwordEncoder.matches(password, instructor.getPassword())) {
            model.addAttribute("error", "Invalid credentials");
            return "admin/admin-login";
        }

        // set admin session
        session.setAttribute("isAdmin", true);
        session.setAttribute("adminId", instructor.getId());

        return "redirect:/admin";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("isAdmin");
        session.removeAttribute("adminId");
        return "redirect:/";
    }
}
