package org.backend.examprep_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class TestDTO {
    private Long testId;
    private String testName;
    private List<DomainDTO> domains;

    // Additional fields for lecturer-created tests
    private String instruction;
    private Integer totalGrade;
    private LocalDateTime dueDate;
    private Integer duration; // Duration in minutes

    // getters and setters
}
