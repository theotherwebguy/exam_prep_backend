package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    // Endpoint to create a new role
    @PostMapping("/create")
    public ResponseEntity<?> createRole(@RequestBody Role role) {
        try {
            // Check if role already exists
            Optional<Role> existingRole = roleService.getRoleByName(role.getName());
            if (existingRole.isPresent()) {
                return ResponseEntity.badRequest().body("Error: Role with this name already exists.");
            }

            // Save the new role
            Role savedRole = roleService.createRole(role);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRole);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: Could not create role. " + e.getMessage());
        }
    }

    // Endpoint to retrieve all roles
    @GetMapping("/all")
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    // Endpoint to retrieve role by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        Optional<Role> role = roleService.getRoleById(id);
        if (role.isPresent()) {
            return ResponseEntity.ok(role.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error: Role not found.");
        }
    }
}