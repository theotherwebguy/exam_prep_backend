package org.backend.examprep_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class TestAttemptDTO {
    private Long testId;
    private String testName;
    private List<QuestionDTO> questions;
}
