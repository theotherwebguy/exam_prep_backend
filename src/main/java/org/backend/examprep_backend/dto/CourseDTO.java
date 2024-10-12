package org.backend.examprep_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CourseDTO {
    private String courseName;
    private String courseDescription;
    private String image; // Image URL
    private List<DomainDTO> domains;
}
