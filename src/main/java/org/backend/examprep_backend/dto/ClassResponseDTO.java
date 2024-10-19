package org.backend.examprep_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class ClassResponseDTO {
    private Long classId;
    private String className;
    private String classDescription;
    private List<StudentResponseDTO> students; // Nested DTO for students
    private CourseResponseDTO course; // Add this field

    // Getters and Setters
}

