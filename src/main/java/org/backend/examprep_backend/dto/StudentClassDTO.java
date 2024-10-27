package org.backend.examprep_backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class StudentClassDTO {
    private Long classId;
    private String className;
    private String classDescription;
    private LocalDate startDate;
    private LocalDate endDate;
    // Getters and setters
}

