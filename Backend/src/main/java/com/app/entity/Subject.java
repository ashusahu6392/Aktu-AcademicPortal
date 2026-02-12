package com.app.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Subject {

    @Id
    @Column(length = 20)
    private String subjectCode;


    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    // Many-to-Many with Branch
    @ManyToMany(mappedBy = "subjects")
    @JsonBackReference("branch-subject")
    private Set<Branch> branches = new HashSet<>();

    // Many-to-Many with Teacher
    @ManyToMany(mappedBy = "subjects")
    @JsonBackReference("teacher-subject")
    private Set<Teacher> teachers = new HashSet<>();

    // One-to-Many with Note
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    @JsonManagedReference("subject-note")
    private List<Note> notes = new ArrayList<>();

    public Subject() {
    }

    public Subject(String subjectCode, String subjectName, Set<Branch> branches, Set<Teacher> teachers, List<Note> notes) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.branches = branches != null ? branches : new HashSet<>();
        this.teachers = teachers != null ? teachers : new HashSet<>();
        this.notes = notes != null ? notes : new ArrayList<>();
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Set<Branch> getBranches() {
        return branches;
    }

    public void setBranches(Set<Branch> branches) {
        this.branches = branches != null ? branches : new HashSet<>();
    }

    public Set<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(Set<Teacher> teachers) {
        this.teachers = teachers != null ? teachers : new HashSet<>();
    }

    public List<Note> getNotes() {
        return notes;
    }

    public void setNotes(List<Note> notes) {
        this.notes = notes != null ? notes : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Subject{" +
                "subjectCode='" + subjectCode + '\'' +
                ", subjectName='" + subjectName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subject subject = (Subject) o;
        return Objects.equals(subjectCode, subject.subjectCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subjectCode);
    }

    // Simple factory to replace builder usage
    public static Subject of(String subjectCode, String subjectName) {
        Subject s = new Subject();
        s.setSubjectCode(subjectCode);
        s.setSubjectName(subjectName);
        return s;
    }

}