package org.backend.examprep_backend.service.unit;

import org.backend.examprep_backend.InvalidRoleException;
import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.dto.StudentClassCourseDTO;
import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.UserRepository;
import org.backend.examprep_backend.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private StudentService studentService;

    private Users student;
    private Role studentRole;

    @BeforeEach
    void setUp() {
        // Create a mock student role
        studentRole = new Role();
        studentRole.setId(1L);
        studentRole.setName("Student");

        // Create a mock student
        student = new Users();
        student.setId(1L);
        student.setFullNames("Test Student");
        student.setEmail("student@example.com");
        student.setContactNumber("123456789");
        student.setProfileImage(new byte[]{(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47}); // Example byte array
        student.setRole(studentRole);
        student.setStudentClasses(new HashSet<>());
    }

    @Test
    void getCourseDetailsForStudent_Success() {
        // Arrange
        Course course = new Course();
        course.setCourseId(1L);
        course.setCourseName("Mathematics");
        course.setCourseDescription("Math course description");
        course.setImage(new byte[]{(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47}); // Example byte array

        Classes class1 = new Classes();
        class1.setClassesId(1L);
        class1.setClassName("Algebra");
        class1.setClassDescription("Algebra class");
        class1.setStartDate(LocalDate.now()); // Using LocalDate for start date
        class1.setEndDate(LocalDate.now().plusMonths(1)); // Using LocalDate for end date

        Classes class2 = new Classes();
        class2.setClassesId(2L);
        class2.setClassName("Geometry");
        class2.setClassDescription("Geometry class");
        class2.setStartDate(LocalDate.now()); // Using LocalDate for start date
        class2.setEndDate(LocalDate.now().plusMonths(1)); // Using LocalDate for end date

        // Add classes to the student
        student.setStudentClasses(new HashSet<>(Arrays.asList(class1, class2)));
        class1.setCourse(course);
        class2.setCourse(course);

        when(userRepository.findById(1L)).thenReturn(Optional.of(student));

        // Act
        StudentClassCourseDTO result = studentService.getCourseDetailsForStudent(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Student", result.getFullName());
        assertEquals("student@example.com", result.getEmail());
        assertEquals(1, result.getCourses().size());
        assertEquals("Mathematics", result.getCourses().get(0).getCourseName());
        assertEquals(2, result.getCourses().get(0).getClasses().size());
    }

    @Test
    void getCourseDetailsForStudent_StudentNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            studentService.getCourseDetailsForStudent(1L);
        });
        assertEquals("Student not found with id: 1", exception.getMessage());
    }

    @Test
    void getCourseDetailsForStudent_UserIsNotStudent() {
        // Arrange
        Role nonStudentRole = new Role();
        nonStudentRole.setId(2L);
        nonStudentRole.setName("LECTURER");

        Users lecturer = new Users();
        lecturer.setId(2L);
        lecturer.setFullNames("Test Lecturer");
        lecturer.setEmail("lecturer@example.com");
        lecturer.setContactNumber("987654321");
        lecturer.setProfileImage(new byte[]{(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47}); // Example byte array
        lecturer.setRole(nonStudentRole);

        when(userRepository.findById(2L)).thenReturn(Optional.of(lecturer));

        // Act & Assert
        InvalidRoleException exception = assertThrows(InvalidRoleException.class, () -> {
            studentService.getCourseDetailsForStudent(2L);
        });
        assertEquals("User is not a Student", exception.getMessage());
    }
}
