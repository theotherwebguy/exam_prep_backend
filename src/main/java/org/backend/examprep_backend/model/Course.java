package org.backend.examprep_backend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
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

    @Lob
    @Column(name = "image", nullable = true)
    private byte[] image;  // Store image as a byte array

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference // This prevents the recursive serialization of the "course" object inside "domains"
    private List<Domain> domains;

    // One-to-Many relationship with Classes
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Classes> classes;

}