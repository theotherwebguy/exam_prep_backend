package org.backend.examprep_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CourseDTO {
    private Long courseId;
    private String courseName;
    private String courseDescription;
    private byte[] image;  // Image URL
    private List<DomainDTO> domains;
}
