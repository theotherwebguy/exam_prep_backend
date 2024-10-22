package org.backend.examprep_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ClassResponseDTO {
    private Long classId;
    private String className;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    private String classDescription;
    private List<StudentResponseDTO> students; // Nested DTO for students
    private CourseResponseDTO course; // Add this field
    private LecturerResponseDTO lecturer;
    // Getters and Setters
}

