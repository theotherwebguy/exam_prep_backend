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

    public Users registerUser(Users user) {

        // Hash password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user to the database
        return userRepository.save(user);
    }

    public Optional<Users> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean authenticateUser(String email, String password) {
        System.out.println("Authenticating user with email:" + email);

        Optional<Users> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            System.out.println("User found: " + user);

            // Check if the password matches
            if (passwordEncoder.matches(password, user.getPassword())) {
                System.out.println("Password matches for user: " + user.getEmail());
                return true; // Password matches, user is authenticated
            } else {
                System.out.println("Password does not match for user: " + user.getEmail());
                throw new IllegalArgumentException("Invalid email or password.");
            }
        } else {
            System.out.println("User not found for email: " + email );
            throw new IllegalArgumentException("User not found with the provided email and tenant ID.");
        }
    }


}
