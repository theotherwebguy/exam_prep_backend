package org.backend.examprep_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class StudentClassCourseDTO {
    private Long studentId;
    private String fullName;
    private String email;
    private byte[] profileImage;
    private String contactNumber;
    private List<StudentCourseDTO> courses;
    // Getters and setters
}

