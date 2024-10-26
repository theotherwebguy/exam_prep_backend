package org.backend.examprep_backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class LecturerClassCourseDTO {
    private Long lecturerId;
    private String lecturerName;
    private String lecturerEmail;
    private String lecturerContactNumber;

    private List<LecturerCourseDTO> courses;

    // Getters and Setters
}

