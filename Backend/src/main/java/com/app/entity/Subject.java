package com.app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"branches", "teachers", "notes"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    // Many-to-Many with Branch
    @ManyToMany(mappedBy = "subjects")
    private Set<Branch> branches = new HashSet<>();

    // Many-to-Many with Teacher
    @ManyToMany(mappedBy = "subjects")
    private Set<Teacher> teachers = new HashSet<>();

    // One-to-Many with Note
    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL)
    private List<Note> notes;
}
