package org.backend.examprep_backend.service;

import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Users registerUser(Users user, String tenantId) {
        // Associate user with the correct tenant
        user.setTenantId(tenantId);

        // Hash password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user to the database
        return userRepository.save(user);
    }

    public Optional<Users> findUserByEmailAndTenantId(String email, String tenantId) {
        return userRepository.findByEmailAndTenantId(email, tenantId);
    }
}
