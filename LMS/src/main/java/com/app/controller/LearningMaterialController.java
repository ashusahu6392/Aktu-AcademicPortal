package com.app.controller;

import com.app.entity.LearningMaterial;
import com.app.entity.Subject;
import com.app.entity.Instructor;
import com.app.repository.SubjectRepository;
import com.app.repository.InstructorRepository;
import com.app.service.LearningMaterialService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/learning-materials")
public class LearningMaterialController {

    private final LearningMaterialService learningMaterialService;
    private final InstructorRepository instructorRepository;
    private final SubjectRepository subjectRepository;

    public LearningMaterialController(LearningMaterialService learningMaterialService, InstructorRepository instructorRepository, SubjectRepository subjectRepository) {
        this.learningMaterialService = learningMaterialService;
        this.instructorRepository = instructorRepository;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping
    public String getAllLearningMaterials(Model model) {
        model.addAttribute("materials", learningMaterialService.getAllLearningMaterials());
        return "home/learning-materials";
    }

    @GetMapping("/add")
    public String addLearningMaterialForm(Model model) {
        model.addAttribute("material", new LearningMaterial());
        model.addAttribute("instructors", instructorRepository.findAll());
        model.addAttribute("subjects", subjectRepository.findAll());
        return "home/add-learning-material";
    }

    @PostMapping("/save")
    public String saveLearningMaterial(@ModelAttribute LearningMaterial lm,
                           @RequestParam Long instructorId,
                           @RequestParam String subjectCode) {

        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new RuntimeException("Instructor not found"));

        Subject subject = subjectRepository.findById(subjectCode)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        lm.setInstructor(instructor);
        lm.setSubject(subject);

        // content sanitization is handled in LearningMaterialServiceImpl
        learningMaterialService.saveLearningMaterial(lm);

        return "redirect:/learning-materials";
    }

}