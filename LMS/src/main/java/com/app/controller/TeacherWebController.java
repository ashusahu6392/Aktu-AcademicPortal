package com.app.controller;

import com.app.dto.TeacherRegisterDto;
import com.app.entity.Note;
import com.app.entity.Subject;
import com.app.entity.Teacher;
import com.app.repository.SubjectRepository;
import com.app.repository.TeacherRepository;
import com.app.service.NoteService;
import com.app.service.TeacherService;
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
@RequestMapping("/teacher")
public class TeacherWebController {

    private final TeacherService teacherService;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;
    private final NoteService noteService;

    public TeacherWebController(TeacherService teacherService,
                                SubjectRepository subjectRepository,
                                TeacherRepository teacherRepository,
                                PasswordEncoder passwordEncoder,
                                NoteService noteService) {
        this.teacherService = teacherService;
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.passwordEncoder = passwordEncoder;
        this.noteService = noteService;
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("teacher", new TeacherRegisterDto());
        model.addAttribute("subjects", subjectRepository.findAll());
        return "teacher-register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("teacher") TeacherRegisterDto dto, Model model) {
        // Basic validation: require name, email, password
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            model.addAttribute("error", "Name is required");
            model.addAttribute("subjects", subjectRepository.findAll());
            return "teacher-register";
        }
        if (dto.getEmail() == null || !dto.getEmail().contains("@")) {
            model.addAttribute("error", "Valid work email is required");
            model.addAttribute("subjects", subjectRepository.findAll());
            return "teacher-register";
        }
        if (dto.getPassword() == null || dto.getPassword().length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters");
            model.addAttribute("subjects", subjectRepository.findAll());
            return "teacher-register";
        }

        // Save via service (it encodes the password)
        teacherService.register(dto);

        return "redirect:/teacher/login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("email", "");
        return "teacher-login";
    }

    @PostMapping("/login")
    public String login(String email, String password, Model model, HttpSession session) {
        if (email == null || password == null) {
            model.addAttribute("error", "Email and password are required");
            return "teacher-login";
        }

        Optional<Teacher> tOpt = teacherRepository.findByEmail(email);
        if (tOpt.isEmpty()) {
            model.addAttribute("error", "Invalid credentials");
            return "teacher-login";
        }

        Teacher teacher = tOpt.get();
        if (!passwordEncoder.matches(password, teacher.getPassword())) {
            model.addAttribute("error", "Invalid credentials");
            return "teacher-login";
        }

        // Simple session login placeholder
        session.setAttribute("teacherId", teacher.getId());

        return "redirect:/teacher/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model, HttpSession session) {
        Object tid = session.getAttribute("teacherId");
        if (tid == null) {
            return "redirect:/teacher/login";
        }
        Long teacherId = (Long) tid;
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher == null) {
            session.removeAttribute("teacherId");
            return "redirect:/teacher/login";
        }

        // Use teacher's subjects for the subject selection
        List<Subject> subjects = List.copyOf(teacher.getSubjects());

        model.addAttribute("teacherProfile", teacher);
        model.addAttribute("subjects", subjects);
        model.addAttribute("note", new Note());

        return "teacher-dashboard";
    }

    @PostMapping("/dashboard/save")
    public String saveNoteFromDashboard(@ModelAttribute Note note,
                                        @RequestParam String subjectCode,
                                        HttpSession session,
                                        Model model) {
        Object tid = session.getAttribute("teacherId");
        if (tid == null) {
            return "redirect:/teacher/login";
        }
        Long teacherId = (Long) tid;
        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);
        if (teacher == null) {
            session.removeAttribute("teacherId");
            return "redirect:/teacher/login";
        }

        Subject subject = subjectRepository.findById(subjectCode).orElse(null);
        if (subject == null) {
            model.addAttribute("error", "Subject not found");
            return "teacher-dashboard";
        }

        note.setTeacher(teacher);
        note.setSubject(subject);

        noteService.saveNote(note);

        return "redirect:/teacher/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
