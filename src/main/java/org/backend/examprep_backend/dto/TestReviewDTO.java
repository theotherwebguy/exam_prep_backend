package org.backend.examprep_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestReviewDTO {
    private Long id;
    private Long studentId;
    private Long testId;
    private Long questionId;

    private Long selectedAnswerId; // Match the model
    private Boolean isCorrect;
    private Integer score; // If you want to return the score

    // Additional constructor or methods can be added if needed
}
