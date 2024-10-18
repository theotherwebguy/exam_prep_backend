package org.backend.examprep_backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Data
public class CourseWithClassesDTO {

    private Long courseId;
    private String courseName;
    private byte[] image;
    private String courseDescription;
    private List<ClassDTO> classes;
}
