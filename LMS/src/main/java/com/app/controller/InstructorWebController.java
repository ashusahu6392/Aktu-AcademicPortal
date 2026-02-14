package com.app.controller;

import com.app.dto.InstructorRegisterDto;
import com.app.entity.LearningMaterial;
import com.app.entity.Subject;
import com.app.entity.Instructor;
import com.app.repository.SubjectRepository;
import com.app.repository.InstructorRepository;
import com.app.repository.LearningMaterialRepository;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

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
    private final LearningMaterialRepository learningMaterialRepository;

    public InstructorWebController(InstructorService instructorService,
                                   SubjectRepository subjectRepository,
                                   InstructorRepository instructorRepository,
                                   PasswordEncoder passwordEncoder,
                                   LearningMaterialService learningMaterialService,
                                   LearningMaterialRepository learningMaterialRepository) {
        this.instructorService = instructorService;
        this.subjectRepository = subjectRepository;
        this.instructorRepository = instructorRepository;
        this.passwordEncoder = passwordEncoder;
        this.learningMaterialService = learningMaterialService;
        this.learningMaterialRepository = learningMaterialRepository;
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

    @GetMapping("/material/add")
    public String showAddMaterialForm(Model model, HttpSession session) {
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

        // Provide the subjects this instructor teaches
        List<Subject> subjects = List.copyOf(instructor.getSubjects());

        model.addAttribute("material", new LearningMaterial());
        model.addAttribute("subjects", subjects);
        model.addAttribute("instructorProfile", instructor);
        model.addAttribute("formAction", "/instructor/material/save");

        return "teacher/add-learning-material";
    }

    // List instructor's uploaded materials
    @GetMapping("/materials")
    public String listMaterials(Model model, HttpSession session) {
        Object iid = session.getAttribute("instructorId");
        if (iid == null) {
            return "redirect:/instructor/login";
        }
        Long instructorId = (Long) iid;
        List<LearningMaterial> materials = learningMaterialService.getLearningMaterialsByInstructor(instructorId);
        model.addAttribute("materials", materials);
        Instructor instr = instructorRepository.findById(instructorId).orElse(null);
        model.addAttribute("instructorProfile", instr);
        return "teacher/my-materials";
    }

    @GetMapping("/material/{id}/edit")
    public String editMaterialForm(@PathVariable("id") Long id, Model model, HttpSession session) {
        Object iid = session.getAttribute("instructorId");
        if (iid == null) {
            return "redirect:/instructor/login";
        }
        Long instructorId = (Long) iid;

        LearningMaterial material = learningMaterialRepository.findById(id).orElse(null);
        if (material == null) {
            return "redirect:/instructor/materials";
        }
        if (!material.getInstructor().getId().equals(instructorId)) {
            return "redirect:/instructor/materials"; // not owner
        }

        Instructor instructor = instructorRepository.findById(instructorId).orElse(null);
        if (instructor == null) return "redirect:/instructor/login";

        model.addAttribute("material", material);
        model.addAttribute("subjects", List.copyOf(instructor.getSubjects()));
        model.addAttribute("instructorProfile", instructor);
        model.addAttribute("formAction", "/instructor/material/" + id + "/update");

        return "teacher/add-learning-material"; // reuse form for editing
    }

    @PostMapping("/material/{id}/update")
    public String updateMaterial(@PathVariable("id") Long id,
                                 @RequestParam("subjectId") String subjectId,
                                 @RequestParam(value = "file", required = false) MultipartFile file,
                                 @ModelAttribute LearningMaterial formData,
                                 HttpSession session,
                                 Model model) {
        Object iid = session.getAttribute("instructorId");
        if (iid == null) {
            return "redirect:/instructor/login";
        }
        Long instructorId = (Long) iid;

        LearningMaterial existing = learningMaterialRepository.findById(id).orElse(null);
        if (existing == null) {
            return "redirect:/instructor/materials";
        }
        if (!existing.getInstructor().getId().equals(instructorId)) {
            return "redirect:/instructor/materials";
        }

        Subject subject = subjectRepository.findById(subjectId).orElse(null);
        if (subject == null) {
            model.addAttribute("error", "Subject not found");
            Instructor instr = instructorRepository.findById(instructorId).orElse(null);
            model.addAttribute("subjects", instr != null ? List.copyOf(instr.getSubjects()) : List.of());
            model.addAttribute("material", existing);
            model.addAttribute("formAction", "/instructor/material/" + id + "/update");
            return "teacher/add-learning-material";
        }

        // Update fields
        existing.setTitle(formData.getTitle());
        existing.setDescription(formData.getDescription());
        existing.setUnitNo(formData.getUnitNo());
        existing.setContent(formData.getContent());
        existing.setSubject(subject);

        if (file != null && !file.isEmpty()) {
            existing.setFileName(file.getOriginalFilename());
            // file storage not implemented here
        }

        learningMaterialService.saveLearningMaterial(existing);

        return "redirect:/instructor/materials";
    }

    @PostMapping("/material/save")
    public String saveMaterial(@ModelAttribute LearningMaterial material,
                               @RequestParam("subjectId") String subjectId,
                               @RequestParam(value = "file", required = false) MultipartFile file,
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

        Subject subject = subjectRepository.findById(subjectId).orElse(null);
        if (subject == null) {
            model.addAttribute("error", "Subject not found");
            model.addAttribute("subjects", List.copyOf(instructor.getSubjects()));
            return "teacher/add-learning-material";
        }

        // If a file was uploaded, record the original filename (storage not implemented here)
        if (file != null && !file.isEmpty()) {
            material.setFileName(file.getOriginalFilename());
            // Optionally: store file bytes or path using a storage service. Skipped for now.
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

    @GetMapping("/overview")
    public String showInstructorOverview(Model model, HttpSession session) {
        Object iid = session.getAttribute("instructorId");
        if (iid == null) {
            return "redirect:/instructor/login";
        }
        Long instructorId;
        try {
            if (iid instanceof Long) instructorId = (Long) iid;
            else if (iid instanceof Integer) instructorId = ((Integer) iid).longValue();
            else instructorId = Long.valueOf(String.valueOf(iid));
        } catch (Exception ex) {
            return "redirect:/instructor/login";
        }

        Instructor instructor = instructorRepository.findById(instructorId).orElse(null);
        if (instructor == null) {
            session.removeAttribute("instructorId");
            return "redirect:/instructor/login";
        }

        // materials authored by this instructor
        List<com.app.entity.LearningMaterial> materials = learningMaterialService.getLearningMaterialsByInstructor(instructorId);

        // totals
        int totalMaterials = materials.size();
        int totalSubjects = instructor.getSubjects() != null ? instructor.getSubjects().size() : 0;

        // per-subject summary: materials count and distinct unit count
        // key by subjectCode
        java.util.Map<String, java.util.Set<Integer>> unitsBySubject = new java.util.HashMap<>();
        java.util.Map<String, Integer> materialsBySubject = new java.util.HashMap<>();
        java.util.Map<String, String> subjectNames = new java.util.HashMap<>();

        for (com.app.entity.LearningMaterial m : materials) {
            if (m.getSubject() == null) continue;
            String code = m.getSubject().getSubjectCode();
            subjectNames.putIfAbsent(code, m.getSubject().getSubjectName());
            materialsBySubject.put(code, materialsBySubject.getOrDefault(code, 0) + 1);
            unitsBySubject.computeIfAbsent(code, k -> new java.util.HashSet<>());
            if (m.getUnitNo() != null) unitsBySubject.get(code).add(m.getUnitNo());
        }

        // Build list of summaries
        java.util.List<java.util.Map<String, Object>> summaries = new java.util.ArrayList<>();
        for (String code : subjectNames.keySet()) {
            java.util.Map<String, Object> s = new java.util.HashMap<>();
            s.put("subjectCode", code);
            s.put("subjectName", subjectNames.get(code));
            s.put("materialsCount", materialsBySubject.getOrDefault(code, 0));
            s.put("unitsCount", unitsBySubject.getOrDefault(code, java.util.Set.of()).size());
            summaries.add(s);
        }

        model.addAttribute("instructorProfile", instructor);
        model.addAttribute("totals", java.util.Map.of("materials", totalMaterials, "subjects", totalSubjects));
        model.addAttribute("subjectsSummary", summaries);

        return "teacher/instructor-overview";
    }

    @PostMapping("/subjects/add")
    public String addSubjectForInstructor(@RequestParam String subjectCode,
                                          @RequestParam String subjectName,
                                          HttpSession session,
                                          Model model) {
        Object iid = session.getAttribute("instructorId");
        if (iid == null) {
            return "redirect:/instructor/login";
        }
        Long instructorId;
        try {
            if (iid instanceof Long) instructorId = (Long) iid;
            else if (iid instanceof Integer) instructorId = ((Integer) iid).longValue();
            else instructorId = Long.valueOf(String.valueOf(iid));
        } catch (Exception ex) {
            return "redirect:/instructor/login";
        }

        Instructor instructor = instructorRepository.findById(instructorId).orElse(null);
        if (instructor == null) {
            session.removeAttribute("instructorId");
            return "redirect:/instructor/login";
        }

        // Basic validation
        subjectCode = subjectCode == null ? "" : subjectCode.trim();
        subjectName = subjectName == null ? "" : subjectName.trim();
        if (subjectCode.isEmpty() || subjectName.isEmpty()) {
            // Return to overview with error (simple approach)
            model.addAttribute("error", "Subject code and name are required");
            return "redirect:/instructor/overview";
        }

        // Prevent duplicates
        boolean exists = subjectRepository.findById(subjectCode).isPresent();
        com.app.entity.Subject subject;
        if (!exists) {
            subject = com.app.entity.Subject.of(subjectCode, subjectName);
            subjectRepository.save(subject);
        } else {
            subject = subjectRepository.findById(subjectCode).get();
        }

        // Associate with instructor if not already
        if (!instructor.getSubjects().contains(subject)) {
            instructor.getSubjects().add(subject);
            instructorRepository.save(instructor);
        }

        return "redirect:/instructor/overview";
    }
}
