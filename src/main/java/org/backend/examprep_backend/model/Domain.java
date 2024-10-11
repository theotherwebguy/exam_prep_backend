package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Entity
public class Domain {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long domainId;

    @ManyToOne
    @JoinColumn(name = "courseId", nullable = false)
    private Course course;

    @Column(length = 255)
    private String domainName;

    @OneToMany(mappedBy = "domain")
    private List<Topic> topics; // List of topics within this domain

    // Getters and Setters
}


