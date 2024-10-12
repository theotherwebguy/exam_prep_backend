package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @Column(nullable = false, unique = true)
    private String courseName;

    @Column(nullable = false)
    private String courseDescription;

    private String image; // URL for course image

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Domain> domains; // Domains associated with the course
}
