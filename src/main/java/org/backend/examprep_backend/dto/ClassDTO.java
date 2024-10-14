package org.backend.examprep_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class ClassDTO {

    @NotBlank
    private String className;

    private String classDescription;

    private LocalDate startDate;

    private LocalDate endDate;

    @NotNull
    private Long courseId;  // Course the class is associated with

    @NotNull
    private Long userId;  // Lecturer responsible for the class

    // Getters and setters
}
