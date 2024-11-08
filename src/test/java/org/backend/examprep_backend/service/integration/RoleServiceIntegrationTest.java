package org.backend.examprep_backend.service.integration;

import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.repository.RoleRepository;
import org.backend.examprep_backend.service.RoleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class RoleServiceIntegrationTest {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testCreateRole() {
        // Create a new role
        Role role = new Role("ROLE_ADMIN");

        // Use RoleService to save the role
        Role savedRole = roleService.createRole(role);

        // Verify the role was saved correctly
        assertNotNull(savedRole);
        assertEquals("ROLE_ADMIN", savedRole.getName());
    }

    @Test
    public void testFindRoleByName() {
        // Given
        Role role = new Role("ROLE_USER");
        roleRepository.save(role);

        // When
        Optional<Role> foundRoleOptional = roleService.getRoleByName("ROLE_USER");

        // Then
        assertTrue(foundRoleOptional.isPresent(), "Role should be found");
        Role foundRole = foundRoleOptional.get(); //safely retrieve the role

        assertNotNull(foundRole);
        assertEquals("ROLE_USER", foundRole.getName());
    }
}
