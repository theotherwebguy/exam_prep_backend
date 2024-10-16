package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "Title is mandatory")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Full names are mandatory")
    @Column(nullable = false)
    private String fullNames;

    @NotBlank(message = "Surname is mandatory")
    @Column(nullable = false)
    private String surname;

    @NotBlank(message = "Contact number is mandatory")
    @Pattern(regexp = "\\+?\\d{10,15}", message = "Contact number should be valid")
    @Column(nullable = false, unique = true)
    private String contactNumber;

    // Use ManyToOne for a single Role
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
//    @NotNull(message = "User must have a role")
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "user_course",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private Set<Course> courses;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "class_id")  // Link the student to the class
    private Classes studentClass;
}
