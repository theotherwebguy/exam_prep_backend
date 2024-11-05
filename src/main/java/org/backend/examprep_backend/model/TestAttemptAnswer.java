package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class TestAttemptAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_attempt_id", nullable = false)
    private TestAttempt testAttempt;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "selected_answer_id", nullable = true) // nullable if unanswered
    private Answer selectedAnswer;

    private Boolean isCorrect;
}
