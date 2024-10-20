package org.backend.examprep_backend.dto;

import lombok.Data;

@Data
public class ClassRequestDTO {
    private String className;
    private String classDescription;
    private Long courseId;  // Optional: Course to link with the class
}

