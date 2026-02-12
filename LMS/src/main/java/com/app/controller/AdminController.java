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

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final CourseRepository courseRepository;
    private final BranchRepository branchRepository;
    private final SubjectRepository subjectRepository;

    public AdminController(AdminService adminService,
                           CourseRepository courseRepository,
                           BranchRepository branchRepository,
                           SubjectRepository subjectRepository) {
        this.adminService = adminService;
        this.courseRepository = courseRepository;
        this.branchRepository = branchRepository;
        this.subjectRepository = subjectRepository;
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
            return "redirect:/"; // redirect to home or login placeholder
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
            return "redirect:/";
        }
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("courseDto", new CourseDto());
        return "admin/courses";
    }

    @GetMapping("/branches")
    public String manageBranches(Model model, HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/";
        }
        model.addAttribute("branches", branchRepository.findAll());
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("branchDto", new BranchDto());
        return "admin/branches";
    }

    @GetMapping("/subjects")
    public String manageSubjects(Model model, HttpSession session) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/";
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
            return "redirect:/";
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
            return "redirect:/";
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
            return "redirect:/";
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
            return "redirect:/";
        }
        adminService.deleteCourse(id);
        redirectAttributes.addFlashAttribute("success", "Course deleted");
        return "redirect:/admin/courses";
    }

    @PostMapping("/branch/delete")
    public String deleteBranch(@RequestParam Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/";
        }
        adminService.deleteBranch(id);
        redirectAttributes.addFlashAttribute("success", "Branch deleted");
        return "redirect:/admin/branches";
    }

    @PostMapping("/subject/delete")
    public String deleteSubject(@RequestParam String code, HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminLoggedIn(session)) {
            return "redirect:/";
        }
        adminService.deleteSubject(code);
        redirectAttributes.addFlashAttribute("success", "Subject deleted");
        return "redirect:/admin/subjects";
    }

    // Temporary test login endpoint (DEV ONLY) - sets admin session and redirects to admin dashboard
    @GetMapping("/test-login")
    public String testLogin(HttpSession session) {
        session.setAttribute("isAdmin", true);
        return "redirect:/admin";
    }
}
