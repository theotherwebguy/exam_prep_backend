package org.backend.examprep_backend.service.unit;

import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.repository.RoleRepository;
import org.backend.examprep_backend.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRole() {
        // Arrange
        Role role = new Role();
        role.setName("Admin");
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        // Act
        Role createdRole = roleService.createRole(role);

        // Assert
        assertEquals("Admin", createdRole.getName());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void testGetAllRoles() {
        // Arrange
        Role role1 = new Role();
        role1.setName("Admin");
        Role role2 = new Role();
        role2.setName("User");
        when(roleRepository.findAll()).thenReturn(List.of(role1, role2));

        // Act
        List<Role> roles = roleService.getAllRoles();

        // Assert
        assertEquals(2, roles.size());
        assertEquals("Admin", roles.get(0).getName());
        assertEquals("User", roles.get(1).getName());
        verify(roleRepository, times(1)).findAll();
    }

    @Test
    void testGetRoleByName() {
        // Arrange
        Role role = new Role();
        role.setName("Admin");
        when(roleRepository.findByName("Admin")).thenReturn(Optional.of(role));

        // Act
        Optional<Role> foundRole = roleService.getRoleByName("Admin");

        // Assert
        assertTrue(foundRole.isPresent());
        assertEquals("Admin", foundRole.get().getName());
        verify(roleRepository, times(1)).findByName("Admin");
    }

    @Test
    void testGetRoleById() {
        // Arrange
        Role role = new Role();
        role.setId(1L);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        // Act
        Optional<Role> foundRole = roleService.getRoleById(1L);

        // Assert
        assertTrue(foundRole.isPresent());
        assertEquals(1L, foundRole.get().getId());
        verify(roleRepository, times(1)).findById(1L);
    }
}