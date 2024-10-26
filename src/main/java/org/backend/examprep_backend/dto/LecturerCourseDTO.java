package org.backend.examprep_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class LecturerCourseDTO {
    private Long courseId;
    private String courseName;
    private String courseDescription;
    private byte[] image;

    private List<ClassWithStudentsDTO> classes;

    // Getters and Setters
}
