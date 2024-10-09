package org.backend.examprep_backend.integration;

import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll(); // Clean database before each test
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        String userJson = "{ \"title\": \"Mr.\", \"email\": \"example@test.com\", \"password\": \"securePassword123\", \"fullNames\": \"John\", \"surname\": \"Doe\", \"contactNumber\": \"+1234567890\", \"roles\": [\"STUDENT\"] }";

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isOk());
    }

    @Test
    void testRegisterUser_MissingPassword() throws Exception {
        String userJson = "{ \"email\": \"integration@test.com\", \"fullNames\": \"Jane Doe\", \"surname\": \"Doe\", \"contactNumber\": \"+1234567890\", \"roles\": [\"STUDENT\"] }"; // Removed password field

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isBadRequest()) // Expecting 400 for missing password
                .andExpect(result ->
                        result.getResponse().getContentAsString().contains("Error: Password cannot be null or empty.")); // Verify the error message
    }
}
