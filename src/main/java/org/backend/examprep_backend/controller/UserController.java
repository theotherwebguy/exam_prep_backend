package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users user, @RequestParam String tenantId) {
        try {

            // Check if user already exists with the same email and tenantId
            Optional<Users> existingUser = userService.findUserByEmailAndTenantId(user.getEmail(), tenantId);
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest().body("Error: A user with this email already exists for the specified tenant.");
            }

            // Register the user if all validations pass
            userService.registerUser(user, tenantId);
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


    @GetMapping("/{email}")
    public ResponseEntity<?> getUser(@PathVariable String email, @RequestParam String tenantId) {
        return userService.findUserByEmailAndTenantId(email, tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
