package org.backend.examprep_backend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topicId;

    @ManyToOne
    @JoinColumn(name = "domainId", nullable = false)
    private Domain domain;

    private String topicName;

    // Getters and Setters
}

