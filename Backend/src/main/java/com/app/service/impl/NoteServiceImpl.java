package com.app.service.impl;

import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

import com.app.entity.Note;
import com.app.repository.NoteRepository;
import com.app.service.NoteService;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public Note saveNote(Note note) {
    	String safeHtml = Jsoup.clean(note.getContent(), Safelist.basicWithImages());
        note.setContent(safeHtml);
        return noteRepository.save(note);
    }

    @Override
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    @Override
    public List<Note> getNotesBySubject(Long subjectId) {
        return noteRepository.findBySubjectId(subjectId);
    }

    @Override
    public List<Note> getNotesByTeacher(Long teacherId) {
        return noteRepository.findByTeacherId(teacherId);
    }
}

