package org.backend.examprep_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDetailDto {
    private Long id;
    private String email;
    private String password;
    private String title;
    private String fullNames;
    private String surname;
    private String contactNumber;
    private String role;
    private byte[] profileImage;
    private List<Long> courseIds;
    private List<Long> classIds;
    private List<CourseDTO> courses;
    private List<ClassDTO> classes;
}
