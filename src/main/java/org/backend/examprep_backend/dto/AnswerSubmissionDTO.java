package org.backend.examprep_backend.dto;

import lombok.Data;

@Data
public class AnswerSubmissionDTO {
    private Long questionId;
    private Long answerId;
    private boolean selected;
}