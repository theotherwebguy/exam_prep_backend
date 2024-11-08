package org.backend.examprep_backend.service.integration;

import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.RoleRepository;
import org.backend.examprep_backend.repository.UserRepository;
import org.backend.examprep_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class SecurityTest {

    private MockMvc mockMvc;  // MockMvc for simulating HTTP requests

    @Autowired
    private UserService userService;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        // Setup MockMvc without @Autowired, using standaloneSetup
        mockMvc = MockMvcBuilders.standaloneSetup(userService).build();  // Setup MockMvc
    }

    @Test
    public void testAdminAccess() throws Exception {
        // Given: Create an admin role if not exists
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));

        // Given: Create an admin user
        Users admin = new Users();
        admin.setEmail("admin@example.com");
        admin.setFullNames("Admin User");
        admin.setRole(adminRole);
        userRepository.save(admin);

        // When & Then: Simulate an authenticated admin user and test access
        mockMvc.perform(get("/admin/dashboard")  // Example protected endpoint
                        .with(user("admin@example.com").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    public void testLecturerAccess() throws Exception {
        // Given: Create a lecturer role if not exists
        Role lecturerRole = roleRepository.findByName("ROLE_LECTURER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_LECTURER")));

        // Given: Create a lecturer user
        Users lecturer = new Users();
        lecturer.setEmail("lecturer@example.com");
        lecturer.setFullNames("Lecturer User");
        lecturer.setRole(lecturerRole);
        userRepository.save(lecturer);

        // When & Then: Simulate an authenticated lecturer user and test access
        mockMvc.perform(get("/lecturer/dashboard")  // Example protected endpoint
                        .with(user("lecturer@example.com").roles("LECTURER")))
                .andExpect(status().isOk());
    }

    @Test
    public void testUnauthorizedUserAccess() throws Exception {
        // Given: Create a student role if not exists
        Role studentRole = roleRepository.findByName("ROLE_STUDENT")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_STUDENT")));

        // Given: Create a student user
        Users student = new Users();
        student.setEmail("student@example.com");
        student.setFullNames("Student User");
        student.setRole(studentRole);
        userRepository.save(student);

        // When & Then: Simulate an unauthorized user (student) trying to access admin endpoint
        mockMvc.perform(get("/admin/dashboard")  // Example protected endpoint
                        .with(user("student@example.com").roles("STUDENT")))
                .andExpect(status().isForbidden());  // Expecting 403 Forbidden
    }

    @Test
    public void testRoleBasedAccessControl() throws Exception {
        // Given: Role "ROLE_USER"
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));

        // Given: Create a user
        Users user = new Users();
        user.setEmail("user@example.com");
        user.setFullNames("User Example");
        user.setRole(userRole);
        userRepository.save(user);

        // When & Then: Simulate a user with ROLE_USER trying to access an endpoint
        mockMvc.perform(get("/user/profile")  // Example endpoint for user
                        .with(user("user@example.com").roles("USER")))
                .andExpect(status().isOk());  // Access allowed
    }

    @Test
    public void testAnonymousAccess() throws Exception {
        // When & Then: Simulate an unauthenticated request
        mockMvc.perform(get("/user/profile"))  // No authentication
                .andExpect(status().isUnauthorized());  // Expecting 401 Unauthorized
    }
}
