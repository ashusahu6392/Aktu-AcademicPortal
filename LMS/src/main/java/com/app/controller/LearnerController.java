package com.app.controller;

import com.app.entity.LearningMaterial;
import com.app.entity.Subject;
import com.app.repository.SubjectRepository;
import com.app.service.LearningMaterialService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/learner")
public class LearnerController {

    private final SubjectRepository subjectRepository;
    private final LearningMaterialService materialService;

    public LearnerController(SubjectRepository subjectRepository, LearningMaterialService materialService) {
        this.subjectRepository = subjectRepository;
        this.materialService = materialService;
    }

    // Learner home - list subjects and default selection
    @GetMapping({"","/"})
    public String index(Model model) {
        List<Subject> subjects = subjectRepository.findAll();
        model.addAttribute("subjects", subjects);
        // No units/topics selected yet
        model.addAttribute("units", Collections.emptyList());
        model.addAttribute("topics", Collections.emptyList());
        model.addAttribute("selectedTopic", null);
        return "learner/index";
    }

    // Show a subject -> compute units (unique unitNo from materials)
    @GetMapping("/subject/{subjectCode}")
    public String showSubject(@PathVariable String subjectCode, Model model) {
        List<Subject> subjects = subjectRepository.findAll();
        List<LearningMaterial> materials = materialService.getLearningMaterialsBySubject(subjectCode);

        // Extract unique unit numbers sorted
        List<Integer> units = materials.stream()
                .map(LearningMaterial::getUnitNo)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("subjects", subjects);
        model.addAttribute("units", units);
        model.addAttribute("topics", Collections.emptyList());
        model.addAttribute("selectedTopic", null);
        model.addAttribute("selectedSubjectCode", subjectCode);

        return "learner/subject";
    }

    // Show topics for a subject unit
    @GetMapping("/subject/{subjectCode}/unit/{unitNo}")
    public String showUnit(@PathVariable String subjectCode, @PathVariable Integer unitNo, Model model) {
        List<Subject> subjects = subjectRepository.findAll();
        List<LearningMaterial> materials = materialService.getLearningMaterialsBySubject(subjectCode);

        List<Integer> units = materials.stream()
                .map(LearningMaterial::getUnitNo)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Filter materials for the selected unit
        List<LearningMaterial> topics = materials.stream()
                .filter(n -> Objects.equals(n.getUnitNo(), unitNo))
                .sorted(Comparator.comparing(LearningMaterial::getUploadDate).reversed())
                .collect(Collectors.toList());

        model.addAttribute("subjects", subjects);
        model.addAttribute("units", units);
        model.addAttribute("topics", topics);
        model.addAttribute("selectedTopic", null);
        model.addAttribute("selectedSubjectCode", subjectCode);
        model.addAttribute("selectedUnitNo", unitNo);

        return "learner/subject"; // reuse subject page to show units + topics
    }

    // Show a single topic (material) content
    @GetMapping("/topic/{id}")
    public String showTopic(@PathVariable Long id, Model model) {
        List<Subject> subjects = subjectRepository.findAll();
        LearningMaterial material = materialService.getAllLearningMaterials().stream()
                .filter(n -> Objects.equals(n.getId(), id))
                .findFirst()
                .orElse(null);

        // If material not found, redirect to index (showing subjects)
        if (material == null) {
            model.addAttribute("subjects", subjects);
            model.addAttribute("units", Collections.emptyList());
            model.addAttribute("topics", Collections.emptyList());
            model.addAttribute("selectedTopic", null);
            model.addAttribute("error", "Topic not found");
            return "learner/index";
        }

        // Build units and topics for the material's subject to show sidebar state
        String subjectCode = material.getSubject().getSubjectCode();
        List<LearningMaterial> subjectMaterials = materialService.getLearningMaterialsBySubject(subjectCode);
        List<Integer> units = subjectMaterials.stream()
                .map(LearningMaterial::getUnitNo)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<LearningMaterial> topics = subjectMaterials.stream()
                .filter(n -> Objects.equals(n.getUnitNo(), material.getUnitNo()))
                .sorted(Comparator.comparing(LearningMaterial::getUploadDate).reversed())
                .collect(Collectors.toList());

        model.addAttribute("subjects", subjects);
        model.addAttribute("units", units);
        model.addAttribute("topics", topics);
        model.addAttribute("selectedTopic", material);
        model.addAttribute("selectedSubjectCode", subjectCode);
        model.addAttribute("selectedUnitNo", material.getUnitNo());

        return "learner/topic";
    }
}
