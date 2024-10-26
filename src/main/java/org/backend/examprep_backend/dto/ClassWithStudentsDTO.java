package org.backend.examprep_backend.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ClassWithStudentsDTO {
    private Long classId;
    private String className;
    private String classDescription;
    private LocalDate startDate;
    private LocalDate endDate;

    private List<StudentResponseDTO> students;

    // Getters and Setters
}

