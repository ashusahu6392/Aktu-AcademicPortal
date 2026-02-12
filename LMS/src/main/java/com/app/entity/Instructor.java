package com.app.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "instructor")
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String role = "ROLE_INSTRUCTOR";

    @ManyToMany
    @JoinTable(
            name = "instructor_subject",
            joinColumns = @JoinColumn(name = "instructor_id"),
            inverseJoinColumns = @JoinColumn(name = "subject_id")
    )
    @JsonManagedReference("instructor-subject")
    private Set<Subject> subjects = new HashSet<>();

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("instructor-learningmaterial")
    private List<LearningMaterial> learningMaterials = new ArrayList<>();

    public Instructor() {
    }

    public Instructor(Long id, String name, String email, String password, String role, Set<Subject> subjects, List<LearningMaterial> learningMaterials) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role != null ? role : "ROLE_INSTRUCTOR";
        this.subjects = subjects != null ? subjects : new HashSet<>();
        this.learningMaterials = learningMaterials != null ? learningMaterials : new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Set<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects != null ? subjects : new HashSet<>();
    }

    public List<LearningMaterial> getLearningMaterials() {
        return learningMaterials;
    }

    public void setLearningMaterials(List<LearningMaterial> learningMaterials) {
        this.learningMaterials = learningMaterials != null ? learningMaterials : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Instructor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instructor that = (Instructor) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
