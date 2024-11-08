package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class AnswerSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_submission_id", nullable = false)
    private TestSubmission testSubmission;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "answer_id", nullable = false)
    private TestAnswer answer;

    @Column(nullable = false)
    private Boolean selected;

    private Boolean isCorrect;
}
