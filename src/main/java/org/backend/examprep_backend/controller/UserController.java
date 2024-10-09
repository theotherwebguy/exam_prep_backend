package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users user) {
        try {
            // Ensure roles are set before saving
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                return ResponseEntity.badRequest().body("Error: User must have at least one role.");
            }

            // Check if user already exists with the same email and tenantId
            Optional<Users> existingUser = userService.findUserByEmail(user.getEmail());
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body("Error: A user with this email already exists.");
            }

            // Validate password
            if (user.getPassword() == null || user.getPassword().isEmpty()) { // NEW CODE
                return ResponseEntity.badRequest().body("Error: Password cannot be null or empty."); // NEW CODE
            }

            // Register the user if all validations pass
            userService.registerUser(user);

            return ResponseEntity.ok("User registered successfully.");

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: User with this email or contact number already exists."+ e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again."+ e.getMessage());
        }


    }

    // Catch validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Create a message that combines all validation errors
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
        System.out.println("Login attempt for email: " + email );

        try {
            // Authenticate user
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
            System.out.println("Unexpected error during login for email: " + email +  ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred. Please try again. " + e.getMessage());
        }
    }

}
