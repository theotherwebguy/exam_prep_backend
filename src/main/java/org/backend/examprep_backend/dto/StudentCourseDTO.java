package org.backend.examprep_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class StudentCourseDTO {
    private Long courseId;
    private String courseName;
    private String courseDescription;
    private byte[] image;
    private List<StudentClassDTO> classes;
    // Getters and setters
}

