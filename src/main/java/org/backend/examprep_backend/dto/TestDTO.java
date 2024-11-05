package org.backend.examprep_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class TestDTO {
    private Long testId;
    private String testName;
    private List<DomainDTO> domains;
    // getters and setters
}

