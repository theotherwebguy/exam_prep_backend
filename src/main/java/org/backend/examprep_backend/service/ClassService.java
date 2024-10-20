package org.backend.examprep_backend.service;

import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.backend.examprep_backend.InvalidRoleException;
import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.dto.*;
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

    @Transactional
    public Classes addClassAndStudents(Long courseId, ClassDTO classDTO, MultipartFile file) throws Exception {
        // Step 1: Fetch course by ID
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Fetch the lecturer responsible for the class
        Users lecturer = userRepository.findById(classDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + classDTO.getUserId()));

        // Validate that the user is a lecturer
        if (!lecturer.getRole().getName().equalsIgnoreCase("Lecturer")) {
            throw new InvalidRoleException("User is not a Lecturer");
        }

        // Step 3: Create the class entity
        Classes newClass = new Classes();
        newClass.setClassName(classDTO.getClassName());
        newClass.setClassDescription(classDTO.getClassDescription());
        newClass.setStartDate(classDTO.getStartDate());
        newClass.setEndDate(classDTO.getEndDate());
        newClass.setCourse(course);
        newClass.setLecturer(lecturer);

        Classes createdClass = classRepository.save(newClass); // Save class

        // Step 4: Parse students from Excel and associate with class
        if (file != null && !file.isEmpty()) {
            Optional<Role> studentRoleOpt = roleRepository.findByName("STUDENT");  // Assuming role ID for student is 1
            if (studentRoleOpt.isEmpty()) {
                throw new ResourceNotFoundException("Student role not found");
            }

            // Extract students from the Excel file and link them to the created class
            Role studentRole = studentRoleOpt.get();
            try {
                // Extract students from Excel and link to the class
                List<Users> students = studentExcelParserService.extractStudentsFromExcel(file, studentRole, createdClass.getClassesId());
                userRepository.saveAll(students);
            } catch (Exception e) {
                // Handle parsing exceptions by throwing a specific error
                throw new Exception("Failed to parse students from Excel: " + e.getMessage(), e);
            }
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
    @Transactional
    public ClassResponseDTO getClassWithStudentsById(Long classId) {
        Classes classEntity = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with ID: " + classId));
        return mapToClassResponseDTO(classEntity);
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

    @Transactional
    public ClassResponseDTO updateClass(Long classId, ClassRequestDTO classRequestDTO) {
        // Find the class by ID or throw an exception
        Classes existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found with ID: " + classId));

        // Update the class fields
        existingClass.setClassName(classRequestDTO.getClassName());
        existingClass.setClassDescription(classRequestDTO.getClassDescription());

        // If the course ID is provided, link the course to the class
        if (classRequestDTO.getCourseId() != null) {
            Course course = courseRepository.findById(classRequestDTO.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found with ID: " + classRequestDTO.getCourseId()));
            existingClass.setCourse(course);
        }

        // Save the updated class
        Classes updatedClass = classRepository.save(existingClass);
        return mapToClassResponseDTO(updatedClass);
    }

    @Transactional
    public void deleteClass(Long classId) {
        if (!classRepository.existsById(classId)) {
            throw new RuntimeException("Class not found with ID: " + classId);
        }
        classRepository.deleteById(classId);
    }


    @Autowired
    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    public List<EnrolledClassDTO> getEnrolledClasses(Long studentId) {
        return classRepository.findEnrolledClassesByStudentId(studentId);
    }




}
