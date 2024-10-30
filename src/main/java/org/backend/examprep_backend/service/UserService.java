package org.backend.examprep_backend.service;

import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.dto.ClassDTO;
import org.backend.examprep_backend.dto.CourseDTO;
import org.backend.examprep_backend.dto.UserDetailDto;
import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.repository.ClassRepository;
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

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IndependentTestService independentTestService;

    @Autowired
    private ClassRepository classRepository;

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

    // GET all user information by Id
    @Transactional(readOnly = true)
    public UserDetailDto findUserById(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Map Users to UserDto
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setTitle(user.getTitle());
        userDto.setFullNames(user.getFullNames());
        userDto.setSurname(user.getSurname());
        userDto.setContactNumber(user.getContactNumber());
        userDto.setProfileImage(user.getProfileImage());

        if (user.getRole() != null) {
            userDto.setRole(user.getRole().getName());
        }

        // Set profile image only if it exists
        if (user.getProfileImage() != null) {
            userDto.setProfileImage(user.getProfileImage());
        }

        // Fetch courses
        List<CourseDTO> courses = getCoursesByUserId(userId); //independentTestService.getCoursesByUserId(userId)

        // Fetch classes
        List<ClassDTO> classes = getClassesByUserId(userId);

        // Create UserDetailDto
        UserDetailDto userDetailDto = new UserDetailDto();
        userDetailDto.setUser(userDto);
        userDetailDto.setCourses(courses);
        userDetailDto.setClasses(classes);

        return userDetailDto;
    }

    // GET all users
    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
    return userRepository.findAll().stream()
            .map(this::mapToUserDto)
            .collect(Collectors.toList());
}
    private UserDto mapToUserDto(Users user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getPassword());
        userDto.setTitle(user.getTitle());
        userDto.setFullNames(user.getFullNames());
        userDto.setSurname(user.getSurname());
        userDto.setContactNumber(user.getContactNumber());
        userDto.setRole(user.getRole().getName());

        // Return the image as byte[] without converting to Base64
        userDto.setProfileImage(user.getProfileImage());

        if (user.getCourses() != null) {
            List<Long> courseIds = user.getCourses().stream()
                    .map(Course::getCourseId)
                    .collect(Collectors.toList());
            userDto.setCourseIds(courseIds);
        }

        return userDto;
    }

    // PUT / Update a user by ID
    @Transactional
    public void updateUser(Long userId, UserDto userDto) {
        // Retrieve the current user data
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Set new values while retaining existing ones
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getFullNames() != null) {
            user.setFullNames(userDto.getFullNames());
        }
        if (userDto.getSurname() != null) {
            user.setSurname(userDto.getSurname());
        }
        if (userDto.getContactNumber() != null) {
            user.setContactNumber(userDto.getContactNumber());
        }
        if (userDto.getTitle() != null) {
            user.setTitle(userDto.getTitle());
        }

        // Update password only if provided
        if (userDto.getPassword() != null && !userDto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        // Update role only if different
        if (!user.getRole().getName().equals(userDto.getRole())) {
            Role role = roleRepository.findByName(userDto.getRole())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));
            user.setRole(role);
        }

        // Update profile image only if provided
        if (userDto.getProfileImage() != null && userDto.getProfileImage().length > 0) {
            user.setProfileImage(userDto.getProfileImage());
        }

        // Save the updated user
        userRepository.save(user);
    }

    //DELETE a user
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

    // Method to authenticate a user
    @Transactional(readOnly = true)
    public UserDto authenticateUser(String email, String password) {
        Optional<Users> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Map Users to UserDto excluding the password
                return mapUserToDto(user);
            } else {
                throw new IllegalArgumentException("Invalid email or password.");
            }
        } else {
            throw new IllegalArgumentException("User not found with the provided email.");
        }
    }

    // Helper method to map Users entity to UserDto
    private UserDto mapUserToDto(Users user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setTitle(user.getTitle());
        userDto.setFullNames(user.getFullNames());
        userDto.setSurname(user.getSurname());
        userDto.setContactNumber(user.getContactNumber());
        userDto.setRole(user.getRole().getName()); // Get role name, not ID
        userDto.setProfileImage(user.getProfileImage());
        userDto.setCourseIds(user.getCourses().stream().map(Course::getCourseId).collect(Collectors.toList()));
        return userDto;
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

    // Method to fetch classes for a specific user by their ID
    @Transactional(readOnly = true)
    public List<ClassDTO> getClassesByUserId(Long userId) {
        List<Classes> classes = classRepository.findByLecturerId(userId);

        return classes.stream().map(clazz -> {
            ClassDTO classDTO = new ClassDTO();
            classDTO.setClassesId(clazz.getClassesId());
            classDTO.setClassName(clazz.getClassName());
            classDTO.setClassDescription(clazz.getClassDescription());
            classDTO.setStartDate(clazz.getStartDate());
            classDTO.setEndDate(clazz.getEndDate());
            classDTO.setCourseId(clazz.getCourse().getCourseId());
            classDTO.setCourseName(clazz.getCourse().getCourseName());
            classDTO.setUserId(clazz.getLecturer().getId());
            classDTO.setLecturerName(clazz.getLecturer().getFullNames());
            classDTO.setLecturerEmail(clazz.getLecturer().getEmail());

            return classDTO;
        }).collect(Collectors.toList());
    }

    // Method to fetch courses for a specific user by their ID
    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByUserId(Long userId) {
        List<Course> courses = courseRepository.findCoursesByUserId(userId);

        return courses.stream().map(course -> {
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setCourseId(course.getCourseId());
            courseDTO.setCourseName(course.getCourseName());
            courseDTO.setCourseDescription(course.getCourseDescription());


            return courseDTO;
        }).collect(Collectors.toList());
    }
}
