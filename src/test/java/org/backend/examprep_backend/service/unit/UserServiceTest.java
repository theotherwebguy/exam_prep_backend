package org.backend.examprep_backend.service.unit;

import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.dto.ClassDTO;
import org.backend.examprep_backend.dto.CourseDTO;
import org.backend.examprep_backend.dto.UserDetailDto;
import org.backend.examprep_backend.dto.UserDto;
import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.ClassRepository;
import org.backend.examprep_backend.repository.CourseRepository;
import org.backend.examprep_backend.repository.RoleRepository;
import org.backend.examprep_backend.repository.UserRepository;
import org.backend.examprep_backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ClassRepository classRepository;


    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private Users user;
    private UserDto userDto;
    private Role role;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        role = new Role();
        role.setId(1L);
        role.setName("STUDENT");

        user = new Users();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFullNames("Test User");
        user.setSurname("User");
        user.setContactNumber("123456789");
        user.setRole(role);
        user.setCourses(new HashSet<>());

        userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setFullNames("Test User");
        userDto.setSurname("User");
        userDto.setContactNumber("123456789");
        userDto.setRole("STUDENT");
        userDto.setCourseIds(Collections.singletonList(1L));
    }

    @Test
    void registerUser_Success() {
        when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(role));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(new Course()));
        when(passwordEncoder.encode(userDto.getEmail())).thenReturn("hashedPassword");
        when(userRepository.save(any(Users.class))).thenReturn(user);

        Users savedUser = userService.registerUser(userDto, new byte[0]);

        assertNotNull(savedUser);
        assertEquals("test@example.com", savedUser.getEmail());
        verify(userRepository, times(1)).save(any(Users.class));
    }

    @Test
    void findUserByEmail_UserFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<Users> foundUser = userService.findUserByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void findUserByEmail_UserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        Optional<Users> foundUser = userService.findUserByEmail("test@example.com");

        assertFalse(foundUser.isPresent());
    }

//    @Test
//    void findUserById_UserFound() {
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//
//        UserDetailDto userDetail = userService.findUserById(1L);
//
//        assertNotNull(userDetail);
//        assertEquals("Test User", userDetail.getUser().getFullNames());
//    }

    @Test
    void findUserById_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.findUserById(2L);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void updateUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(role));

        userService.updateUser(1L, userDto);

        verify(userRepository, times(1)).save(any(Users.class));
        assertEquals("Test User", user.getFullNames());
    }

    @Test
    void updateUser_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(2L, userDto);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_UserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.deleteUser(2L);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void findUserByEmailOrContactNumber_UserFoundByEmail() {
        when(userRepository.findByEmailOrContactNumber("test@example.com", "123456789")).thenReturn(Optional.of(user));

        Optional<Users> foundUser = userService.findUserByEmailOrContactNumber("test@example.com", "123456789");

        assertTrue(foundUser.isPresent());
    }

//    @Test
//    void authenticateUser_Success() {
//        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
//        when(passwordEncoder.matches(eq("password"), eq(user.getEmail()))).thenReturn(true);
//
//        UserDto authenticatedUser = userService.authenticateUser("test@example.com", "password");
//
//        assertNotNull(authenticatedUser);
//        assertEquals("test@example.com", authenticatedUser.getEmail());
//    }

    @Test
    void authenticateUser_UserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.authenticateUser("test@example.com", "password");
        });

        assertEquals("User not found with the provided email.", exception.getMessage());
    }

    @Test
    void authenticateUser_InvalidPassword() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(String.class), any(String.class))).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.authenticateUser("test@example.com", "wrongPassword");
        });

        assertEquals("Invalid email or password.", exception.getMessage());
    }

//    @Test
//    void getClassesByUserId() {
//        Classes clazz = new Classes();
//        clazz.setClassesId(1L);
//        clazz.setClassName("Class 1");
//        clazz.setCourse(new Course());
//        clazz.getCourse().setCourseId(1L);
//        List<Classes> classes = Collections.singletonList(clazz);
//        when(classRepository.findByLecturerId(1L)).thenReturn(classes);
//
//        List<ClassDTO> result = userService.getClassesByUserId(1L);
//
//        assertEquals(1, result.size());
//        assertEquals("Class 1", result.get(0).getClassName());
//    }


    @Test
    void getCoursesByUserId() {
        Course course = new Course();
        course.setCourseId(1L);
        course.setCourseName("Course 1");
        List<Course> courses = Collections.singletonList(course);
        when(courseRepository.findCoursesByUserId(1L)).thenReturn(courses);

        List<CourseDTO> courseDTOs = userService.getCoursesByUserId(1L);

        assertEquals(1, courseDTOs.size());
        assertEquals("Course 1", courseDTOs.get(0).getCourseName());
    }
}
