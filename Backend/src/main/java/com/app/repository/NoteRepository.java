package com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.app.entity.Note;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findBySubjectId(Long subjectId);

    List<Note> findByTeacherId(Long teacherId);
}
