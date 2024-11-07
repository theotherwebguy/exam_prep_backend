package org.backend.examprep_backend.service.unit;

import jakarta.transaction.Transactional;
//import org.apache.poi.ss.usermodel.MultipartFile;
import org.backend.examprep_backend.InvalidRoleException;
import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.dto.ClassDTO;
import org.backend.examprep_backend.dto.ClassResponseDTO;
import org.backend.examprep_backend.dto.StudentResponseDTO;
import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.ClassRepository;
import org.backend.examprep_backend.repository.CourseRepository;
import org.backend.examprep_backend.repository.RoleRepository;
import org.backend.examprep_backend.repository.UserRepository;
import org.backend.examprep_backend.service.ClassService;
import org.backend.examprep_backend.service.StudentExcelParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClassServiceTest {

    @Mock
    private ClassRepository classRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentExcelParserService studentExcelParserService;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private ClassService classService;

    private Course course;
    private Users lecturer;
    private ClassDTO classDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Set up mock data for tests
        course = new Course();
        course.setCourseId(1L);
        course.setCourseName("Test Course");

        lecturer = new Users();
        lecturer.setId(1L);
        lecturer.setFullNames("John Doe");
        lecturer.setRole(new Role(1L, "Lecturer"));

        classDTO = new ClassDTO();
        classDTO.setClassName("Test Class");
        classDTO.setClassDescription("Description");
        classDTO.setStartDate(LocalDate.parse("2024-01-01"));
        classDTO.setEndDate(LocalDate.parse("2024-12-31"));
        classDTO.setUserId(1L);
    }

//    @Test
//    void testAddClassAndStudents() throws Exception {
//        // Mock the dependencies
//        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
//        when(userRepository.findById(1L)).thenReturn(Optional.of(lecturer));
//        when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(new Role(2L, "STUDENT")));
//        when(studentExcelParserService.extractStudentsFromExcel(any(), any(), any())).thenReturn(new ArrayList<>());
//
//        // Call the method under test
//        Classes createdClass = classService.addClassAndStudents(1L, classDTO, null);
//
//        // Verify interactions and assertions
//        verify(classRepository).save(any(Classes.class));
//        assertNotNull(createdClass);
//        assertEquals("Test Class", createdClass.getClassName());
//        assertEquals(lecturer, createdClass.getLecturer());
//    }

    @Test
    void testAddClassAndStudents_courseNotFound() {
        // Mock course not found
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        // Call and assert exception
        assertThrows(ResourceNotFoundException.class, () -> {
            classService.addClassAndStudents(1L, classDTO, null);
        });
    }

    @Test
    void testAddClassAndStudents_lecturerNotFound() {
        // Mock user not found
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Call and assert exception
        assertThrows(ResourceNotFoundException.class, () -> {
            classService.addClassAndStudents(1L, classDTO, null);
        });
    }

//    @Test
//    void testAddClassAndStudents_invalidRole() {
//        // Mock lecturer role mismatch
//        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
//        when(userRepository.findById(1L)).thenReturn(Optional.of(lecturer));
//        when(lecturer.getRole().getName()).thenReturn("Student");  // Incorrect role
//
//        // Call and assert exception
//        assertThrows(InvalidRoleException.class, () -> {
//            classService.addClassAndStudents(1L, classDTO, null);
//        });
//    }

    @Test
    void testGetAllClassesWithStudents() {
        // Mock repository
        Classes mockClass = new Classes();
        mockClass.setClassName("Mock Class");
        when(classRepository.findAll()).thenReturn(List.of(mockClass));

        // Call method
        List<ClassResponseDTO> classes = classService.getAllClassesWithStudents();

        // Verify the result
        assertNotNull(classes);
        assertEquals(1, classes.size());
        assertEquals("Mock Class", classes.get(0).getClassName());
    }

    @Test
    void testGetClassWithStudentsById_notFound() {
        // Mock class not found
        when(classRepository.findById(1L)).thenReturn(Optional.empty());

        // Call and assert exception
        assertThrows(RuntimeException.class, () -> {
            classService.getClassWithStudentsById(1L);
        });
    }

    // Add more tests for other methods (e.g., deleteClass, updateClass)
}
