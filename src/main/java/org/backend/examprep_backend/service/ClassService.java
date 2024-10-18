package org.backend.examprep_backend.service;

import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.backend.examprep_backend.InvalidRoleException;
import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.dto.ClassDTO;
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

    public List<Classes> getAllClasses() {
        return classRepository.findAll();
    }

    public List<Classes> getClassesByCourseId(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
        return classRepository.findByCourse(course);
    }

    public Classes getClassById(Long classId) {
        return classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));
    }
    @Transactional
    public Classes updateClass(Long classId, ClassDTO classDTO) {
        // Find the class by ID
        Classes existingClass = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));

        // Update the class details
        existingClass.setClassName(classDTO.getClassName());
        existingClass.setClassDescription(classDTO.getClassDescription());
        existingClass.setStartDate(classDTO.getStartDate());
        existingClass.setEndDate(classDTO.getEndDate());

        // If the lecturer is being updated, fetch the lecturer and validate their role
        if (classDTO.getUserId() != null) {
            Users lecturer = userRepository.findById(classDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + classDTO.getUserId()));

            if (!lecturer.getRole().getName().equalsIgnoreCase("Lecturer")) {
                throw new InvalidRoleException("User is not a Lecturer");
            }

            existingClass.setLecturer(lecturer);
        }

        // Save the updated class
        Classes savedClass = classRepository.save(existingClass);

        // Populate the DTO with the updated data, including the course name
        ClassDTO updatedClassDTO = new ClassDTO();
        updatedClassDTO.setClassesId(savedClass.getClassesId());
        updatedClassDTO.setClassName(savedClass.getClassName());
        updatedClassDTO.setClassDescription(savedClass.getClassDescription());
        updatedClassDTO.setStartDate(savedClass.getStartDate());
        updatedClassDTO.setEndDate(savedClass.getEndDate());

        if (savedClass.getLecturer() != null) {
            updatedClassDTO.setUserId(savedClass.getLecturer().getId());
            updatedClassDTO.setLecturerName(savedClass.getLecturer().getFullNames());
        }

        if (savedClass.getCourse() != null) {
            updatedClassDTO.setCourseName(savedClass.getCourse().getCourseName()); // Fetch course name
        }

        return savedClass;
    }
    public void deleteClass(Long classId) {
        // Check if the class exists before deleting
        Classes classToDelete = classRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + classId));

        // Delete the class
        classRepository.delete(classToDelete);
    }


}
