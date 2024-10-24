package org.backend.examprep_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassDTO {

    private Long classesId;
    @NotBlank
    private String className;

    private String classDescription;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @NotNull
    private Long courseId;  // Course the class is associated with

    @NotNull
    private Long userId;  // Lecturer responsible for the class

    private String courseName; // New field for the course name
    private String lecturerName; // Add lecturer name
    private String lecturerEmail; // Add lecturer email

    // Add student IDs to track which students are associated with the class
    private List<Long> studentIds;

    // Getters and setters
}
