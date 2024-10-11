package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @ManyToOne
    @JoinColumn(name = "userId")  // Lecturer reference
    private Users lecturer;

    // Getters and Setters
}