package org.backend.examprep_backend.dto;

import lombok.Data;

@Data
public class CourseDTO {
    private Long courseId;          // Optional: Include if you want to send back the ID after creation
    private String courseName;      // The name of the course
    private String courseDescription; // Description of the course
    private String image;           // URL or path to th
}
