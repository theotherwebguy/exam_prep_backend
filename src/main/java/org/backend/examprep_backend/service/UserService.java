package org.backend.examprep_backend.service;

import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.repository.CourseRepository;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import org.backend.examprep_backend.dto.UserDto; // Make sure to import UserDto
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.repository.RoleRepository;
import org.backend.examprep_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    // Register a new user using UserDto
    @Transactional
    public Users registerUser(UserDto userDto,  byte[] profileImage) {
        // Create a new Users entity from UserDto
        Users user = new Users();
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setTitle(userDto.getTitle());
        user.setFullNames(userDto.getFullNames());
        user.setSurname(userDto.getSurname());
        user.setContactNumber(userDto.getContactNumber());
        user.setProfileImage(profileImage);

        // Hash the user's password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Fetch the role from the database
        Role role = roleRepository.findByName(userDto.getRole())
                .orElseThrow(() -> new IllegalArgumentException("Error: Role not found"));

        user.setRole(role); // Assign the fetched role to the user

        // Fetch and assign courses based on courseIds
        Set<Course> courses = new HashSet<>();
        for (Long courseId : userDto.getCourseIds()) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
            courses.add(course);
        }
        user.setCourses(courses);  // Set the user's courses
        // Save the user to the database
        return userRepository.save(user);
    }

    // Find a user by email
    @Transactional(readOnly = true)
    public Optional<Users> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // Gt all users
    @Transactional(readOnly = true)
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    //Update a user
    @Transactional
    public void updateUser(Long userId, UserDto userDto) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setEmail(userDto.getEmail());
        user.setFullNames(userDto.getFullNames());
        user.setSurname(userDto.getSurname());
        user.setContactNumber(userDto.getContactNumber());
        user.setTitle(userDto.getTitle());

        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        Role role = roleRepository.findByName(userDto.getRole())
                .orElseThrow(() -> new IllegalArgumentException("Role not found"));
        user.setRole(role);

        userRepository.save(user);
    }

    //Delete a user
    @Transactional
    public void deleteUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userRepository.delete(user);
    }


    // Find a user by email or contact number
    @Transactional(readOnly = true)
    public Optional<Users> findUserByEmailOrContactNumber(String email, String contactNumber) {
        return userRepository.findByEmailOrContactNumber(email, contactNumber);
    }

    // Authenticate a user
    public boolean authenticateUser(String email, String password) {
        Optional<Users> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return true; // Password matches
            } else {
                throw new IllegalArgumentException("Invalid email or password.");
            }
        } else {
            throw new IllegalArgumentException("User not found with the provided email.");
        }
    }

    // Assign courses to a user
    @Transactional
    public void assignCoursesToUser(Long userId, List<Course> courses) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate user role before assigning courses
        if (user.getRole().getName().equalsIgnoreCase("Student")) {
            user.setCourses(new HashSet<>(courses)); // Students can have multiple courses
        } else if (user.getRole().getName().equalsIgnoreCase("Moderator")) {
            if (courses.size() > 1) {
                throw new IllegalArgumentException("Moderators can only be assigned to one course.");
            }
            user.setCourses(new HashSet<>(courses)); // Assign the single course
        } else {
            throw new IllegalArgumentException("Invalid role for course assignment.");
        }

        userRepository.save(user); // Save the updated user
    }

    // Method to search for lecturers by name using binary search
    @Transactional
    public Optional<Users> binarySearchLecturerByName(String surname) {
        List<Users> lecturers = userRepository.findAllByRoleName("LECTURER"); // Fetch all lecturers
        // Ensure the list is sorted by name for binary search to work
        lecturers.sort(Comparator.comparing(Users::getSurname));
        int left = 0;
        int right = lecturers.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Users midUser = lecturers.get(mid);
            if (midUser.getSurname().equalsIgnoreCase(surname)) {
                return Optional.of(midUser);
            } else if (midUser.getSurname().compareToIgnoreCase(surname) < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return Optional.empty(); // Lecturer not found
    }

    // Method to get all lecturers
    @Transactional(readOnly = true)
    public List<Users> getAllLecturers() {
        return userRepository.findAllByRoleName("LECTURER");
    }
}
