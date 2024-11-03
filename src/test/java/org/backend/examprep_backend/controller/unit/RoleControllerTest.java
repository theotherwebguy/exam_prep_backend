package org.backend.examprep_backend.controller.unit;

import org.backend.examprep_backend.controller.RoleController;
import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RoleControllerTest {

    @Mock
    private RoleService roleService;

    @InjectMocks
    private RoleController roleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateRole() {
        // Arrange
        Role role = new Role();
        role.setName("Admin");
        when(roleService.getRoleByName("Admin")).thenReturn(Optional.empty());
        when(roleService.createRole(any(Role.class))).thenReturn(role);

        // Act
        ResponseEntity<?> response = roleController.createRole(role);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(role, response.getBody());
        verify(roleService, times(1)).createRole(role);
    }

    @Test
    void testGetAllRoles() {
        // Arrange
        Role role1 = new Role();
        role1.setName("Admin");
        Role role2 = new Role();
        role2.setName("User");
        when(roleService.getAllRoles()).thenReturn(List.of(role1, role2));

        // Act
        ResponseEntity<List<Role>> response = roleController.getAllRoles();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetRoleById() {
        // Arrange
        Role role = new Role();
        role.setId(1L);
        when(roleService.getRoleById(1L)).thenReturn(Optional.of(role));

        // Act
        ResponseEntity<?> response = roleController.getRoleById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(role, response.getBody());
    }
}

