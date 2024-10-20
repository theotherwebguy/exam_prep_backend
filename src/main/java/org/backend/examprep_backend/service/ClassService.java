package org.backend.examprep_backend.service;

import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.backend.examprep_backend.InvalidRoleException;
import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.dto.ClassDTO;
import org.backend.examprep_backend.dto.ClassResponseDTO;
import org.backend.examprep_backend.dto.CourseResponseDTO;
import org.backend.examprep_backend.dto.StudentResponseDTO;
import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Role;
import org.backend.examprep_backend.repository.ClassRepository;
import org.backend.examprep_backend.repository.UserRepository;
import org.backend.examprep_backend.repository.CourseRepository;
import org.backend.examprep_backend.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClassService {

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentExcelParserService studentExcelParserService;


    @Autowired
    private RoleRepository roleRepository;

    public Classes addClassAndStudents(Long courseId, ClassDTO classDTO, MultipartFile file) throws Exception {
        // Step 1: Create the class
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Fetch the lecturer responsible for the class
        Users lecturer = userRepository.findById(classDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + classDTO.getUserId()));

        // Validate that the user is a lecturer
        if (!lecturer.getRole().getName().equalsIgnoreCase("Lecturer")) {
            throw new InvalidRoleException("User is not a Lecturer");
        }

        // Create and populate the class entity
        Classes newClass = new Classes();
        newClass.setClassName(classDTO.getClassName());
        newClass.setClassDescription(classDTO.getClassDescription());
        newClass.setStartDate(classDTO.getStartDate());
        newClass.setEndDate(classDTO.getEndDate());
        newClass.setCourse(course);
        newClass.setLecturer(lecturer);

        // Save the class to the database
        Classes createdClass = classRepository.save(newClass);

        // Step 2: Parse the uploaded Excel file for students and associate them with the class
        if (file != null && !file.isEmpty()) {
            Optional<Role> studentRoleOpt = roleRepository.findByName("STUDENT");  // Assuming role ID for student is 1
            if (studentRoleOpt.isEmpty()) {
                throw new ResourceNotFoundException("Student role not found");
            }

            // Extract students from the Excel file and link them to the created class
            Role studentRole = studentRoleOpt.get();
            List<Users> students = studentExcelParserService.extractStudentsFromExcel(file, studentRole, createdClass.getClassesId());

            // Save the students in the database
            userRepository.saveAll(students);
        }

        // Return the newly created class
        return createdClass;
    }
@Transactional
    public List<ClassResponseDTO> getAllClassesWithStudents() {
        List<Classes> classEntities = classRepository.findAll();

        // Map classes to DTOs
        return classEntities.stream()
                .map(this::mapToClassResponseDTO)
                .toList();
    }
    private ClassResponseDTO mapToClassResponseDTO(Classes classes) {
        ClassResponseDTO dto = new ClassResponseDTO();
        dto.setClassId(classes.getClassesId());
        dto.setClassName(classes.getClassName());
        dto.setClassDescription(classes.getClassDescription());

        // Map students to StudentResponseDTO
        List<StudentResponseDTO> studentDTOs = classes.getStudents().stream()
                .map(this::mapToStudentResponseDTO)
                .toList();
        dto.setStudents(studentDTOs);
// Map the course information
        if (classes.getCourse() != null) {
            CourseResponseDTO courseDTO = new CourseResponseDTO();
            courseDTO.setCourseId(classes.getCourse().getCourseId());
            courseDTO.setCourseName(classes.getCourse().getCourseName());
            courseDTO.setCourseDescription(classes.getCourse().getCourseDescription());
            dto.setCourse(courseDTO);
        }

        return dto;
    }
    private StudentResponseDTO mapToStudentResponseDTO(Users student) {
        StudentResponseDTO dto = new StudentResponseDTO();
        dto.setStudentId(student.getId());
        dto.setFullName(student.getFullNames());
        dto.setEmail(student.getEmail());
        dto.setContactNumber(student.getContactNumber());
        return dto;
    }



}
