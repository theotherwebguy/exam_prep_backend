package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody Users user, @RequestParam String tenantId) {
        try {
            userService.registerUser(user, tenantId);
            return ResponseEntity.ok("User registered successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error registering user: " + e.getMessage());
        }
    }

    @GetMapping("/{email}")
    public ResponseEntity<?> getUser(@PathVariable String email, @RequestParam String tenantId) {
        return userService.findUserByEmailAndTenantId(email, tenantId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
