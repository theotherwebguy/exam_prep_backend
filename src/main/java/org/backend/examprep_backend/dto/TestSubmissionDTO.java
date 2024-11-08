package org.backend.examprep_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class TestSubmissionDTO {
    private Long testId;
    private Long studentId;
    private Integer score;
    private Boolean submitted;
    private int totalScore;
    private List<TestQuestionDTO> testQuestions;
    private List<AnswerSubmissionDTO> answerSubmissions;
}