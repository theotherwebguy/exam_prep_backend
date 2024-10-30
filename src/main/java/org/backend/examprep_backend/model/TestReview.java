package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "test_review")
public class TestReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;
    private Long testId;
    private Long questionId;

    @Column(name = "selected_answer_id") // Assuming this is the ID of the selected answer
    private Long selectedAnswerId; // Change to ID type

    private Boolean isCorrect;
    private Integer score; // If you want to store the score as well

    // Additional constructor or methods can be added if needed
}
