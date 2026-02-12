package com.app.service.impl;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

import com.app.entity.LearningMaterial;
import com.app.repository.LearningMaterialRepository;
import com.app.service.LearningMaterialService;

@Service
public class LearningMaterialServiceImpl implements LearningMaterialService {

    private final LearningMaterialRepository learningMaterialRepository;

    public LearningMaterialServiceImpl(LearningMaterialRepository learningMaterialRepository) {
        this.learningMaterialRepository = learningMaterialRepository;
    }

    @Override
    public LearningMaterial saveLearningMaterial(LearningMaterial lm) {
        String safeHtml = Jsoup.clean(lm.getContent(), Safelist.basicWithImages());
        lm.setContent(safeHtml);
        return learningMaterialRepository.save(lm);
    }

    @Override
    public List<LearningMaterial> getAllLearningMaterials() {
        return learningMaterialRepository.findAll();
    }

    @Override
    public List<LearningMaterial> getLearningMaterialsBySubject(String subjectId) {
        return learningMaterialRepository.findBySubjectSubjectCode(subjectId);
    }

    @Override
    public List<LearningMaterial> getLearningMaterialsByInstructor(Long instructorId) {
        return learningMaterialRepository.findByInstructorId(instructorId);
    }
}
