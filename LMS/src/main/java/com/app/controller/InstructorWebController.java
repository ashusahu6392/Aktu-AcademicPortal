package com.app.controller;

import com.app.dto.InstructorRegisterDto;
import com.app.entity.LearningMaterial;
import com.app.entity.Subject;
import com.app.entity.Instructor;
import com.app.repository.SubjectRepository;
import com.app.repository.InstructorRepository;
import com.app.service.LearningMaterialService;
import com.app.service.InstructorService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/instructor")
public class InstructorWebController {

    private final InstructorService instructorService;
    private final SubjectRepository subjectRepository;
    private final InstructorRepository instructorRepository;
    private final PasswordEncoder passwordEncoder;
    private final LearningMaterialService learningMaterialService;

    public InstructorWebController(InstructorService instructorService,
                                   SubjectRepository subjectRepository,
                                   InstructorRepository instructorRepository,
                                   PasswordEncoder passwordEncoder,
                                   LearningMaterialService learningMaterialService) {
        this.instructorService = instructorService;
        this.subjectRepository = subjectRepository;
        this.instructorRepository = instructorRepository;
        this.passwordEncoder = passwordEncoder;
        this.learningMaterialService = learningMaterialService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("instructor", new InstructorRegisterDto());
        model.addAttribute("subjects", subjectRepository.findAll());
        return "teacher/instructor-register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("instructor") InstructorRegisterDto dto, Model model) {
        // Basic validation: require name, email, password
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            model.addAttribute("error", "Name is required");
            model.addAttribute("subjects", subjectRepository.findAll());
            return "teacher/instructor-register";
        }
        if (dto.getEmail() == null || !dto.getEmail().contains("@")) {
            model.addAttribute("error", "Valid work email is required");
            model.addAttribute("subjects", subjectRepository.findAll());
            return "teacher/instructor-register";
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters");
            model.addAttribute("subjects", subjectRepository.findAll());
            return "teacher/instructor-register";
        }

        // Save via service (it encodes the password)
        instructorService.register(dto);

        return "redirect:/instructor/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("email", "");
        return "teacher/instructor-login";
    }

    @PostMapping("/login")
    public String login(String email, String password, Model model, HttpSession session) {
        if (email == null || password == null) {
            model.addAttribute("error", "Email and password are required");
            return "teacher/instructor-login";
        }

        Optional<Instructor> tOpt = instructorRepository.findByEmail(email);
        if (tOpt.isEmpty()) {
            model.addAttribute("error", "Invalid credentials");
            return "teacher/instructor-login";
        }

        Instructor instructor = tOpt.get();
        if (!passwordEncoder.matches(password, instructor.getPassword())) {
            model.addAttribute("error", "Invalid credentials");
            return "teacher/instructor-login";
        }

        // Simple session login placeholder
        session.setAttribute("instructorId", instructor.getId());

        return "redirect:/instructor/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        Object iid = session.getAttribute("instructorId");
        if (iid == null) {
            return "redirect:/instructor/login";
        }
        Long instructorId = (Long) iid;
        Instructor instructor = instructorRepository.findById(instructorId).orElse(null);
        if (instructor == null) {
            session.removeAttribute("instructorId");
            return "redirect:/instructor/login";
        }

        // Use instructor's subjects for the subject selection
        List<Subject> subjects = List.copyOf(instructor.getSubjects());

        model.addAttribute("instructorProfile", instructor);
        model.addAttribute("subjects", subjects);
        model.addAttribute("material", new LearningMaterial());

        return "teacher/instructor-dashboard";
    }

    @PostMapping("/dashboard/save")
    public String saveMaterialFromDashboard(@ModelAttribute LearningMaterial material,
                                        @RequestParam String subjectCode,
                                        HttpSession session,
                                        Model model) {
        Object iid = session.getAttribute("instructorId");
        if (iid == null) {
            return "redirect:/instructor/login";
        }
        Long instructorId = (Long) iid;
        Instructor instructor = instructorRepository.findById(instructorId).orElse(null);
        if (instructor == null) {
            session.removeAttribute("instructorId");
            return "redirect:/instructor/login";
        }

        Subject subject = subjectRepository.findById(subjectCode).orElse(null);
        if (subject == null) {
            model.addAttribute("error", "Subject not found");
            return "teacher/instructor-dashboard";
        }

        material.setInstructor(instructor);
        material.setSubject(subject);

        learningMaterialService.saveLearningMaterial(material);

        return "redirect:/instructor/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
