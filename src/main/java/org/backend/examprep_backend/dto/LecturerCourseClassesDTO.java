package org.backend.examprep_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class LecturerCourseClassesDTO {
    private Long courseId;
    private String courseName;
    private String courseDescription;
    private byte[] image;
    private List<ClassResponseDTO> classes; // List of classes under this course
    // Getters and Setters
}

