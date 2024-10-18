package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.UserDto;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.RoleRepository;
import org.backend.examprep_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    // Register
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto) {
        try {
            // Ensure role is set before saving
            if (userDto.getRole() == null || userDto.getRole().isEmpty()) {
                return ResponseEntity.badRequest().body("Error: User must have a role.");
            }

            // Check if user already exists with the same email
            Optional<Users> existingUser = userService.findUserByEmail(userDto.getEmail());
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body("Error: A user with this email already exists.");
            }

            // Validate password
            if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body("Error: Password cannot be null or empty.");
            }

            // Fetch the role from the database
            Role role = roleRepository.findByName(userDto.getRole())
                    .orElseThrow(() -> new IllegalArgumentException("Error: Role not found"));

            // Set the role in the user DTO before passing it to the service
            userDto.setRole(role.getName()); // Set the role name in the DTO

            // Register the user
            userService.registerUser(userDto); // Pass the DTO to UserService
            return ResponseEntity.ok("User registered successfully.");

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: User with this email or contact number already exists." + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again." + e.getMessage());
        }
    }

    // Get all users
    @GetMapping("/all")
    public ResponseEntity<List<Users>> getAllUsers() {
        List<Users> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Update user
    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        try {
            userService.updateUser(userId, userDto);
            return ResponseEntity.ok("User updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again." + e.getMessage());
        }
    }

    // Delete user
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again." + e.getMessage());
        }
    }

    // Assign Course
    @PostMapping("/{userId}/assignCourses")
    public ResponseEntity<?> assignCourses(@PathVariable Long userId, @RequestBody List<Course> courses) {
        try {
            userService.assignCoursesToUser(userId, courses);
            return ResponseEntity.ok("Courses assigned successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again." + e.getMessage());
        }
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder("Validation failed: ");
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ")
        );

        return ResponseEntity.badRequest().body(errorMessage.toString());
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> getUser(@PathVariable String email) {
        return userService.findUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        System.out.println("Login attempt for email: " + email);

        try {
            boolean isAuthenticated = userService.authenticateUser(email, password);

            if (isAuthenticated) {
                System.out.println("Login successful for user: " + email);
                return ResponseEntity.ok("Login successful.");
            } else {
                System.out.println("Authentication failed for user: " + email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Error during login for email: " + email + ": " + e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Unexpected error during login for email: " + email + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again. " + e.getMessage());
        }
    }
}
