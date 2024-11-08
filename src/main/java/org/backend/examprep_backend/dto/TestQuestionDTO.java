package org.backend.examprep_backend.dto;

import lombok.Data;

@Data
public class TestQuestionDTO {
    private String questionText;
    private String selectedAnswer;
    private String correctAnswer;
}
