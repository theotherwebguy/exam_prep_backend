package org.backend.examprep_backend.repository.integration;

import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.RoleRepository;
import org.backend.examprep_backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")  // Use application-test.properties configuration

public class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testSaveAndFindUserById() {
        Users user = new Users();
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFullNames("Test User");
        user.setSurname("Example");

        // Save the user
        Users savedUser = userRepository.save(user);

        // Retrieve by ID
        Users retrievedUser = userRepository.findById(savedUser.getId()).orElse(null);
        assertNotNull(retrievedUser);
        assertEquals("test@example.com", retrievedUser.getEmail());
    }

    @Test
    public void testFindByEmail() {

        // Create and save a role
        Role role = new Role();
        role.setName("ROLE_USER");
        Role savedRole = roleRepository.save(role);


        Users user = new Users();
        user.setEmail("unique@example.com");
        user.setPassword("password123");
        user.setFullNames("Unique User");
        user.setSurname("Example");
        user.setContactNumber("123456789");
        user.setTitle("Example Title");
        user.setRole(savedRole);

        // Save the user
        userRepository.save(user);

        // Retrieve by email
        Optional<Users> retrievedUser = userRepository.findByEmail("unique@example.com");
        assertTrue(retrievedUser.isPresent());
        assertEquals("unique@example.com", retrievedUser.get().getEmail());
    }
}