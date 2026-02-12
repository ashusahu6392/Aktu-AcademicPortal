package com.app.controller;

import com.app.entity.Note;
import com.app.entity.Subject;
import com.app.entity.Teacher;
import com.app.repository.SubjectRepository;
import com.app.repository.TeacherRepository;
import com.app.service.NoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;
    private final TeacherRepository teacherRepository;
    private final SubjectRepository subjectRepository;

    public NoteController(NoteService noteService, TeacherRepository teacherRepository, SubjectRepository subjectRepository) {
        this.noteService = noteService;
        this.teacherRepository = teacherRepository;
        this.subjectRepository = subjectRepository;
    }

    @GetMapping
    public String getAllNotes(Model model) {
        model.addAttribute("notes", noteService.getAllNotes());
        return "notes";
    }

    @GetMapping("/add")
    public String addNoteForm(Model model) {
        model.addAttribute("note", new Note());
        model.addAttribute("teachers", teacherRepository.findAll());
        model.addAttribute("subjects", subjectRepository.findAll());
        return "add-note";
    }

    @PostMapping("/save")
    public String saveNote(@ModelAttribute Note note,
                           @RequestParam Long teacherId,
                           @RequestParam String subjectCode) {

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        Subject subject = subjectRepository.findById(subjectCode)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        note.setTeacher(teacher);
        note.setSubject(subject);

        // content sanitization is handled in NoteServiceImpl
        noteService.saveNote(note);

        return "redirect:/notes";
    }

}