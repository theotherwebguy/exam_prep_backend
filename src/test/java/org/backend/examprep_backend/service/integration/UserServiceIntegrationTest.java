package org.backend.examprep_backend.service.integration;

import org.backend.examprep_backend.dto.UserDto;
import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.RoleRepository;
import org.backend.examprep_backend.repository.UserRepository;
import org.backend.examprep_backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @Transactional
    public void testCreateUserWithRole() {
        //Check if the role already exists
        Role role = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            //If role does not exist, create and save it
            Role newRole = new Role("ROLE_USER");
            return roleRepository.save(newRole);
        });
        // Create and save a role
//        Role role = new Role("ROLE_USER");
//        Role savedRole = roleRepository.save(role);

        // Create a user and set the role
        UserDto userDto = new UserDto();
        userDto.setEmail("user@example.com");
        userDto.setPassword("password123");
        userDto.setFullNames("Test User");
        userDto.setSurname("Example");
        userDto.setRole(role.getName()); // Assign the saved role
        userDto.setContactNumber("123456789");
        userDto.setCourseIds(new ArrayList<>()); // Make sure it's initialized to an empty list

        // Use UserService to save the user
        Users savedUser = userService.registerUser(userDto,new byte[0]); // You can pass an empty byte array for profile image

        // Assert that the user was saved correctly
        assertNotNull(savedUser);
        assertEquals("user@example.com", savedUser.getEmail());
        assertEquals(role.getId(), savedUser.getRole().getId());
    }

    @Test
    public void testFindUserByEmail() {
        // Given
        // Check if the role already exists
        Role role = roleRepository.findByName("ROLE_USER").orElseGet(() -> {
            // If role does not exist, create and save it
            Role newRole = new Role("ROLE_USER");
            return roleRepository.save(newRole);
        });

        // Create a user and assign the role
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFullNames("Test User");
        user.setSurname("Example");
        user.setRole(role);
        userRepository.save(user);

        // When
        Optional<Users> foundUserOptional = userService.findUserByEmail("test@example.com");

        // Then
        assertTrue(foundUserOptional.isPresent(), "User should be found");
        Users foundUser = foundUserOptional.get(); // safely retrieve the user
        assertEquals("test@example.com", foundUser.getEmail());
    }
}

