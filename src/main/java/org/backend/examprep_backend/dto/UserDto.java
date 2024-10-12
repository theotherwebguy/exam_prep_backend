package org.backend.examprep_backend.dto;

import lombok.Getter;
import lombok.Setter;
import org.backend.examprep_backend.model.Role;

@Getter
@Setter
public class UserDto {
    private String email;
    private String password;
    private String title;
    private String fullNames;
    private String surname;
    private String contactNumber;
    private Role role;

    // Getters and Setters
}

