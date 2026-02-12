package com.app.service;

import com.app.entity.LearningMaterial;
import java.util.List;

public interface LearningMaterialService {

    LearningMaterial saveLearningMaterial(LearningMaterial lm);

    List<LearningMaterial> getAllLearningMaterials();

    List<LearningMaterial> getLearningMaterialsBySubject(String subjectId);

    List<LearningMaterial> getLearningMaterialsByInstructor(Long instructorId);
}
