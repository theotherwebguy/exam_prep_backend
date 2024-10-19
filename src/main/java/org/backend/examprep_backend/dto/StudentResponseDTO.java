package org.backend.examprep_backend.dto;

import lombok.Data;

@Data
public class StudentResponseDTO {
    private Long studentId;
    private String fullName;
    private String email;
    private String contactNumber;

    // Getters and Setters
}

