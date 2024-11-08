package org.backend.examprep_backend.service.unit;

import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.ClassRepository;
import org.backend.examprep_backend.repository.UserRepository;
import org.backend.examprep_backend.service.StudentExcelParserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentExcelParserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ClassRepository classRepository;

    @InjectMocks
    private StudentExcelParserService studentExcelParserService;

    private final String excelData = "Email,Name,ContactNumber,Class\n" +
            "student1@example.com,Student One,1234567890,Class A\n" +
            "student2@example.com,Student Two,0987654321,Class B\n";

    private Classes classA;
    private Classes classB;

    @BeforeEach
    public void setUp() {
        // Mock role
        Role studentRole = new Role();
        studentRole.setId(1L);
        studentRole.setName("Student");

        // Initialize classA and classB
        classA = new Classes();
        classA.setClassName("Class A");
        classA.setStudents(new HashSet<>());

        classB = new Classes();
        classB.setClassName("Class B");
        classB.setStudents(new HashSet<>());

        // Mock repository methods
        when(classRepository.findByLecturer(any())).thenReturn(Arrays.asList(classA, classB));
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
    }

    @Test
    public void testExtractStudentsFromExcel() throws Exception {
        // Create a mock MultipartFile
        MultipartFile multipartFile = new MockMultipartFile("file", "students.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", excelData.getBytes());

        // Call the method under test
        studentExcelParserService.extractStudentsFromExcel(multipartFile, new Role(), 1L);

        // Verify interactions with repositories
        ArgumentCaptor<Users> userCaptor = ArgumentCaptor.forClass(Users.class);
        verify(userRepository, times(2)).save(userCaptor.capture());

        // Validate that the users were created with the expected properties
        assertEquals(2, userCaptor.getAllValues().size());

        Users savedUser1 = userCaptor.getAllValues().get(0);
        assertEquals("student1@example.com", savedUser1.getEmail());
        assertEquals("Student One", savedUser1.getFullNames());
        assertEquals("1234567890", savedUser1.getContactNumber());
        assertEquals("Student", savedUser1.getRole().getName());

        Users savedUser2 = userCaptor.getAllValues().get(1);
        assertEquals("student2@example.com", savedUser2.getEmail());
        assertEquals("Student Two", savedUser2.getFullNames());
        assertEquals("0987654321", savedUser2.getContactNumber());
        assertEquals("Student", savedUser2.getRole().getName());

        // Verify that students were added to their respective classes
        assertEquals(1, classA.getStudents().size());
        assertEquals(1, classB.getStudents().size());
    }
}
