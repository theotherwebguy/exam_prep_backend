package org.backend.examprep_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDetailDto {
    private UserDto user;
    private List<CourseDTO> courses;
    private List<ClassDTO> classes;
}
