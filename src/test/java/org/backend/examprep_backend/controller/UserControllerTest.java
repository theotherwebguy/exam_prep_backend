package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private Users testUser;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Sample user object for testing
        testUser = Users.builder()
                .email("test@example.com")
                .password("password")
                .fullNames("John Doe")
                .surname("Doe")
                .contactNumber("+1234567890")
                .roles(Set.of(Role.STUDENT))
                .build();
    }

    @Test
    void testRegisterUser_Success() {
        // Mocking service layer to behave as expected
        when(userService.findUserByEmail(testUser.getEmail())).thenReturn(Optional.empty());
        when(userService.registerUser(any(Users.class))).thenReturn(testUser);

        // Call controller method
        ResponseEntity<?> response = userController.registerUser(testUser);

        // Assertions to verify behavior
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully.", response.getBody());

        // Verify service interaction
        verify(userService, times(1)).registerUser(testUser);
    }

    @Test
    void testRegisterUser_UserExists() {
        // Mock the service to return a user
        when(userService.findUserByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));

        // Call the controller method
        ResponseEntity<?> response = userController.registerUser(testUser);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: A user with this email already exists.", response.getBody());

        // Verify that registerUser method is not called
        verify(userService, never()).registerUser(testUser);
    }

    @Test
    void testRegisterUser_NoRoles() {
        // Set user roles to empty
        testUser.setRoles(Set.of());

        // Call the controller method
        ResponseEntity<?> response = userController.registerUser(testUser);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: User must have at least one role.", response.getBody());
    }
}
