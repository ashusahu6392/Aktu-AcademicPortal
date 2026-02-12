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

    // Many-to-Many with Instructor (was Teacher)
    @ManyToMany(mappedBy = "subjects")
    @JsonBackReference("instructor-subject")
    private Set<Instructor> instructors = new HashSet<>();

    // One-to-Many with LearningMaterial (was Note)
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    @JsonManagedReference("subject-learningmaterial")
    private List<LearningMaterial> learningMaterials = new ArrayList<>();

    public Subject() {
    }

    public Subject(String subjectCode, String subjectName, Set<Branch> branches, Set<Instructor> instructors, List<LearningMaterial> learningMaterials) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.branches = branches != null ? branches : new HashSet<>();
        this.instructors = instructors != null ? instructors : new HashSet<>();
        this.learningMaterials = learningMaterials != null ? learningMaterials : new ArrayList<>();
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

    public Set<Instructor> getInstructors() {
        return instructors;
    }

    public void setInstructors(Set<Instructor> instructors) {
        this.instructors = instructors != null ? instructors : new HashSet<>();
    }

    public List<LearningMaterial> getLearningMaterials() {
        return learningMaterials;
    }

    public void setLearningMaterials(List<LearningMaterial> learningMaterials) {
        this.learningMaterials = learningMaterials != null ? learningMaterials : new ArrayList<>();
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