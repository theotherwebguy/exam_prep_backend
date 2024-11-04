package org.backend.examprep_backend.dto;

import lombok.Data;

import java.util.Map;
@Data
public class SubmitTestRequestDTO {
    private Long testId;
    private Long studentId;
    private Map<Long, Long> answers; // questionId to answerId map
    // getters and setters
}

