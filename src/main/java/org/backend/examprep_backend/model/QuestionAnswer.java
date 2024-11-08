package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class QuestionAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Question question; // The question this answer belongs to

    private String answerText; // The text of the answer

    private boolean isCorrect; // Whether this answer is correct

    // Getters and setters
}
