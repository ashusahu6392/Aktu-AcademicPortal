package com.app.service;

import com.app.entity.Note;
import java.util.List;

public interface NoteService {

    Note saveNote(Note note);

    List<Note> getAllNotes();

    List<Note> getNotesBySubject(String subjectId);

    List<Note> getNotesByTeacher(Long teacherId);
}
