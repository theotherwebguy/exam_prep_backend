package org.backend.examprep_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EnrolledClassDTO {
    private String className;
    private String lecturerName;
    private String courseName;
    private LocalDate startDate;
    private LocalDate endDate;

    // Constructors, getters, and setters
    public EnrolledClassDTO(String className, String lecturerName, String courseName, LocalDate startDate, LocalDate endDate) {
        this.className = className;
        this.lecturerName = lecturerName;
        this.courseName = courseName;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}