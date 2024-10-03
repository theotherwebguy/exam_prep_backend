package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Automatically generate ID
    private Long id; // Primary key

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Column(nullable = false)
    private String password;  // Store hashed password

    @NotBlank(message = "Title is mandatory")
    @Column(nullable = false)
    private String title;  // E.g., Mr., Ms., Dr.

    @NotBlank(message = "Full names are mandatory")
    @Column(nullable = false)
    private String fullNames;

    @NotBlank(message = "Surname is mandatory")
    @Column(nullable = false)
    private String surname;

    @NotBlank(message = "Contact number is mandatory")
    @Pattern(regexp = "\\+?\\d{10,15}", message = "Contact number should be valid") // Adjust the regex as needed
    @Column(nullable = false, unique = true)
    private String contactNumber;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<Role> roles;  // Roles | Student, Instructor, etc.

    @NotBlank(message = "Tenant ID is mandatory")
    @Column(nullable = false)
    private String tenantId;  // Unique ID for each college/tenant
}
