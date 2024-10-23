package org.backend.examprep_backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClassRequestDTO {
    private String className;
    private String classDescription;
    private Long courseId;  // Optional: Course to link with the class
    private Long lecturerId;    // ID of the lecturer
    private LocalDate startDate; // Start date of the class
    private LocalDate endDate;   // End date of the class
}

