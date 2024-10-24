package org.backend.examprep_backend.dto;

import lombok.Data;

@Data
public class CourseResponseDTO {
    private Long courseId;
    private String courseName;
    private String courseDescription;
    private byte[] image;

    // Getters and Setters
}

