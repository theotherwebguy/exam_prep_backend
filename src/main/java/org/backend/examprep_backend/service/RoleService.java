package org.backend.examprep_backend.service;

import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    // Create or save a new role
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    // Get all roles
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // Get role by name
    public Optional<Role> getRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    // Get role by ID
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }
}