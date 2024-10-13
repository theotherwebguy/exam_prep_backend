package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.UserDto;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserDto testUserDto;
    private Users testUser;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);

        // Sample UserDto for testing
        testUserDto = UserDto.builder()
                .email("test@example.com")
                .password("password")
                .title("Mr.")
                .fullNames("John Doe")
                .surname("Doe")
                .contactNumber("+1234567890")
                .role("STUDENT")
                .build();

        // Sample Users entity for mocking service layer responses
        testUser = Users.builder()
                .email("test@example.com")
                .password("password")
                .fullNames("John Doe")
                .surname("Doe")
                .contactNumber("+1234567890")
                .role(new Role("STUDENT"))
                .build();
    }

    @Test
    void testRegisterUser_Success() {
        // Mocking service layer behavior
        when(userService.findUserByEmail(testUserDto.getEmail())).thenReturn(Optional.empty());
        when(userService.registerUser(any(UserDto.class))).thenReturn(testUser);

        // Call the controller method
        ResponseEntity<?> response = userController.registerUser(testUserDto);

        // Assertions to verify behavior
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("User registered successfully.", response.getBody());

        // Verify that the service's registerUser method was called
        verify(userService, times(1)).registerUser(any(UserDto.class));
    }

    @Test
    void testRegisterUser_UserExists() {
        // Mock the service to return an existing user
        when(userService.findUserByEmail(testUserDto.getEmail())).thenReturn(Optional.of(testUser));

        // Call the controller method
        ResponseEntity<?> response = userController.registerUser(testUserDto);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: A user with this email already exists.", response.getBody());

        // Verify that the registerUser method was not called
        verify(userService, never()).registerUser(any(UserDto.class));
    }

    @Test
    void testRegisterUser_NoRole() {
        // Set user role to null
        testUserDto.setRole(null);

        // Call the controller method
        ResponseEntity<?> response = userController.registerUser(testUserDto);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: User must have a role.", response.getBody());
    }

    @Test
    void testRegisterUser_EmptyPassword() {
        // Set an empty password in the DTO
        testUserDto.setPassword("");

        // Call the controller method
        ResponseEntity<?> response = userController.registerUser(testUserDto);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Password cannot be null or empty.", response.getBody());
    }

    @Test
    void testAssignCourses_Success() {
        // Mocking service layer behavior for course assignment
        doNothing().when(userService).assignCoursesToUser(anyLong(), anyList());

        // Call the controller method
        ResponseEntity<?> response = userController.assignCourses(1L, Collections.emptyList());

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Courses assigned successfully.", response.getBody());

        // Verify that the assignCoursesToUser method was called
        verify(userService, times(1)).assignCoursesToUser(anyLong(), anyList());
    }

    @Test
    void testAssignCourses_UserNotFound() {
        // Mock service behavior for user not found
        doThrow(new IllegalArgumentException("User not found")).when(userService).assignCoursesToUser(anyLong(), anyList());

        // Call the controller method
        ResponseEntity<?> response = userController.assignCourses(999L, Collections.emptyList());

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: User not found", response.getBody());
    }

    @Test
    void testAssignCourses_InvalidRole() {
        // Prepare test case for invalid role assignment
        doThrow(new IllegalArgumentException("Invalid role for course assignment.")).when(userService).assignCoursesToUser(anyLong(), anyList());

        // Call the controller method
        ResponseEntity<?> response = userController.assignCourses(1L, Collections.emptyList());

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Error: Invalid role for course assignment.", response.getBody());
    }
}
