package org.backend.examprep_backend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(columnDefinition = "TEXT")
    private String questionText;

    @ManyToOne
    @JoinColumn(name = "domainId", nullable = false)
    private Domain domain;

    @ManyToOne
    @JoinColumn(name = "topicId", nullable = false)
    private Topic topic;

    // Getters and Setters
}

