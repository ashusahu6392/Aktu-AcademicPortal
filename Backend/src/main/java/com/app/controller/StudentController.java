package com.app.controller;

import com.app.entity.Note;
import com.app.entity.Subject;
import com.app.repository.SubjectRepository;
import com.app.service.NoteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final SubjectRepository subjectRepository;
    private final NoteService noteService;

    public StudentController(SubjectRepository subjectRepository, NoteService noteService) {
        this.subjectRepository = subjectRepository;
        this.noteService = noteService;
    }

    // Student home - list subjects and default selection
    @GetMapping({"","/"})
    public String index(Model model) {
        List<Subject> subjects = subjectRepository.findAll();
        model.addAttribute("subjects", subjects);
        // No units/topics selected yet
        model.addAttribute("units", Collections.emptyList());
        model.addAttribute("topics", Collections.emptyList());
        model.addAttribute("selectedTopic", null);
        return "student/index";
    }

    // Show a subject -> compute units (unique unitNo from notes)
    @GetMapping("/subject/{subjectCode}")
    public String showSubject(@PathVariable String subjectCode, Model model) {
        List<Subject> subjects = subjectRepository.findAll();
        List<Note> notes = noteService.getNotesBySubject(subjectCode);

        // Extract unique unit numbers sorted
        List<Integer> units = notes.stream()
                .map(Note::getUnitNo)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        model.addAttribute("subjects", subjects);
        model.addAttribute("units", units);
        model.addAttribute("topics", Collections.emptyList());
        model.addAttribute("selectedTopic", null);
        model.addAttribute("selectedSubjectCode", subjectCode);

        return "student/subject";
    }

    // Show topics for a subject unit
    @GetMapping("/subject/{subjectCode}/unit/{unitNo}")
    public String showUnit(@PathVariable String subjectCode, @PathVariable Integer unitNo, Model model) {
        List<Subject> subjects = subjectRepository.findAll();
        List<Note> notes = noteService.getNotesBySubject(subjectCode);

        List<Integer> units = notes.stream()
                .map(Note::getUnitNo)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Filter notes for the selected unit
        List<Note> topics = notes.stream()
                .filter(n -> Objects.equals(n.getUnitNo(), unitNo))
                .sorted(Comparator.comparing(Note::getUploadDate).reversed())
                .collect(Collectors.toList());

        model.addAttribute("subjects", subjects);
        model.addAttribute("units", units);
        model.addAttribute("topics", topics);
        model.addAttribute("selectedTopic", null);
        model.addAttribute("selectedSubjectCode", subjectCode);
        model.addAttribute("selectedUnitNo", unitNo);

        return "student/subject"; // reuse subject page to show units + topics
    }

    // Show a single topic (note) content
    @GetMapping("/topic/{noteId}")
    public String showTopic(@PathVariable Long noteId, Model model) {
        List<Subject> subjects = subjectRepository.findAll();
        Note note = noteService.getAllNotes().stream()
                .filter(n -> Objects.equals(n.getId(), noteId))
                .findFirst()
                .orElse(null);

        // If note not found, redirect to index (showing subjects)
        if (note == null) {
            model.addAttribute("subjects", subjects);
            model.addAttribute("units", Collections.emptyList());
            model.addAttribute("topics", Collections.emptyList());
            model.addAttribute("selectedTopic", null);
            model.addAttribute("error", "Topic not found");
            return "student/index";
        }

        // Build units and topics for the note's subject to show sidebar state
        String subjectCode = note.getSubject().getSubjectCode();
        List<Note> subjectNotes = noteService.getNotesBySubject(subjectCode);
        List<Integer> units = subjectNotes.stream()
                .map(Note::getUnitNo)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<Note> topics = subjectNotes.stream()
                .filter(n -> Objects.equals(n.getUnitNo(), note.getUnitNo()))
                .sorted(Comparator.comparing(Note::getUploadDate).reversed())
                .collect(Collectors.toList());

        model.addAttribute("subjects", subjects);
        model.addAttribute("units", units);
        model.addAttribute("topics", topics);
        model.addAttribute("selectedTopic", note);
        model.addAttribute("selectedSubjectCode", subjectCode);
        model.addAttribute("selectedUnitNo", note.getUnitNo());

        return "student/topic";
    }
}
