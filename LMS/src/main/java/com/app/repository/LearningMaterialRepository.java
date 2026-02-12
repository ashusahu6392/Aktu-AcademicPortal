package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.entity.LearningMaterial;
import java.util.List;

public interface LearningMaterialRepository extends JpaRepository<LearningMaterial, Long> {

    List<LearningMaterial> findBySubjectSubjectCode(String subjectCode);

    List<LearningMaterial> findByInstructorId(Long instructorId);
}
