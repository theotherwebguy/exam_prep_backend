package org.backend.examprep_backend.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class UserDto {

    private  Long id;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    private String title;

    @NotBlank(message = "Full names are required")
    private String fullNames;

    private String surname;

    @NotBlank(message = "Contact number is required")
    private String contactNumber;

    @NotBlank(message = "Role is required")
    private String role;

    private List<Long> courseIds;

    public UserDto(Long id, String fullNames, String email) {
        this.id = id;
        this.fullNames = fullNames;
        this.email = email;
    }
}