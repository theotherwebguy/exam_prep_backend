package org.backend.examprep_backend.dto;

import lombok.Data;

import java.util.Map;

@Data
public class TestCreationRequestDTO {
    private String testName;
    private Map<Long, Integer> topicQuestionCount; // Map of topic ID to question count

    // getters and setters
}

