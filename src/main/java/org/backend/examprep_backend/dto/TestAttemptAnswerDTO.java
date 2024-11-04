package org.backend.examprep_backend.dto;

import lombok.Data;

@Data
public class TestAttemptAnswerDTO {
    private Long questionId;
    private Long selectedAnswerId;
}
