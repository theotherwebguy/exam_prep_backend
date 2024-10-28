package org.backend.examprep_backend.dto;

import lombok.Data;

@Data
public class LecturerResponseDTO {
    private Long lecturerId;
    private String fullName;
    private String email;
    private String contactNumber;
}
