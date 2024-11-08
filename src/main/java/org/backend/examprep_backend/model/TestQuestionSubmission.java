package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class TestQuestionSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private TestSubmission testSubmission; // The submission this question belongs to

    @ManyToOne
    private Question question; // The question being answered

    @ManyToOne
    private Answer selectedAnswer; // The student's selected answer

    private boolean isCorrect; // Whether the student's answer is correct

    private String explanation; // Optional explanation or feedback on the answer

    // Getters and setters
}

