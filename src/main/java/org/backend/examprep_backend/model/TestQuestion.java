package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class TestQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    private Boolean isCorrect;

    private Long selectedAnswerId;

    private Integer score;

    // getters and setters
}

