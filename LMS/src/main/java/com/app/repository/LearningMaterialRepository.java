package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.app.entity.LearningMaterial;
import java.util.List;

public interface LearningMaterialRepository extends JpaRepository<LearningMaterial, Long> {

    List<LearningMaterial> findBySubjectSubjectCode(String subjectCode);

    List<LearningMaterial> findByInstructorId(Long instructorId);

    long countByInstructorId(Long instructorId);

    @Query("SELECT new com.app.dto.SubjectSummary(l.subject.subjectCode, l.subject.subjectName, COUNT(l), COUNT(DISTINCT l.unitNo)) " +
           "FROM LearningMaterial l WHERE l.instructor.id = :instructorId GROUP BY l.subject.subjectCode, l.subject.subjectName")
    List<com.app.dto.SubjectSummary> findSubjectSummariesByInstructorId(@Param("instructorId") Long instructorId);
}