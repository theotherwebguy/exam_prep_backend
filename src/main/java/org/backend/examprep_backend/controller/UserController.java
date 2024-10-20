package org.backend.examprep_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.backend.examprep_backend.dto.UserDto;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.RoleRepository;
import org.backend.examprep_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;

    // Register user
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerUser(@RequestParam("userDto") String userDtoJson,
                                          @RequestPart("image") MultipartFile image) throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            UserDto userDto = objectMapper.readValue(userDtoJson, UserDto.class);

            long maxSize = 3 * 1024 * 1024; // 3MB limit

            // Validate image
            if (image != null && !image.isEmpty()) {
                if (image.getSize() > maxSize) {
                    return ResponseEntity.badRequest().body("Error: Image size exceeds the maximum limit of 5MB.");
                }
                userDto.setProfileImage(image.getBytes());
            }

            // Validate role
            if (userDto.getRole() == null || userDto.getRole().isEmpty()) {
                return ResponseEntity.badRequest().body("Error: User must have a role.");
            }

            // Check if user already exists
            Optional<Users> existingUser = userService.findUserByEmail(userDto.getEmail());
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body("Error: A user with this email already exists.");
            }

            // Validate password
            if (userDto.getPassword() == null || userDto.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body("Error: Password cannot be null or empty.");
            }

            // Set role from repository
            Role role = roleRepository.findByName(userDto.getRole())
                    .orElseThrow(() -> new IllegalArgumentException("Error: Role not found"));

            userDto.setRole(role.getName()); // Set role in userDto

            // Register user
            userService.registerUser(userDto, userDto.getProfileImage());
            return ResponseEntity.ok("User registered successfully.");

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: User with this email or contact number already exists. " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again. " + e.getMessage());
        }
    }

    // Endpoint to list all lecturers
    @GetMapping("/lecturers")
    public ResponseEntity<List<Users>> listLecturers() {
        List<Users> lecturers = userService.getAllLecturers();
        return ResponseEntity.ok(lecturers);
    }

    // Endpoint to search for a lecturer by surname
    @GetMapping("/lecturers/search")
    public ResponseEntity<?> searchLecturer(@RequestParam("surname") String surname) {
        Optional<Users> lecturer = userService.binarySearchLecturerByName(surname);
        return lecturer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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
