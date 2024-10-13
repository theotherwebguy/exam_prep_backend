package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @Column(length = 255)
    private String courseName;

    @Column(columnDefinition = "TEXT")
    private String courseDescription;

    @Column(length = 255)
    private String image;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Domain> domains; // A course can have many domains

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Classes> classes;  // A course can have multiple classes
    // Getters and Setters
}