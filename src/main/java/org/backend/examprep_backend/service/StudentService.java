package org.backend.examprep_backend.service;

import org.backend.examprep_backend.InvalidRoleException;
import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.dto.StudentClassCourseDTO;
import org.backend.examprep_backend.dto.StudentClassDTO;
import org.backend.examprep_backend.dto.StudentCourseDTO;
import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentService {

    private final UserRepository userRepository;

    @Autowired
    public StudentService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public StudentClassCourseDTO getCourseDetailsForStudent(Long studentId) {
        // Validate if the user exists and has a 'Student' role
        Users student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

//        if (!student.getRole().getName().equalsIgnoreCase("ENROLLSTUDENT")) {
//            throw new InvalidRoleException("User is not a Student");
//        }

        // Prepare the student information
        StudentClassCourseDTO studentDTO = new StudentClassCourseDTO();
        studentDTO.setStudentId(student.getId());
        studentDTO.setFullName(student.getFullNames());
        studentDTO.setEmail(student.getEmail());
        studentDTO.setProfileImage(student.getProfileImage());
        studentDTO.setContactNumber(student.getContactNumber());

        // Fetch all classes the student is enrolled in
        Set<Classes> studentClasses = student.getStudentClasses();

        // Group classes by course to avoid redundant course information
        Map<Course, List<Classes>> coursesWithClasses = studentClasses.stream()
                .collect(Collectors.groupingBy(Classes::getCourse));

        // Build the list of courses with their classes
        List<StudentCourseDTO> courseDTOs = coursesWithClasses.entrySet().stream()
                .map(entry -> {
                    Course course = entry.getKey();
                    List<Classes> classes = entry.getValue();

                    // Create a StudentCourseDTO for each course
                    StudentCourseDTO courseDTO = new StudentCourseDTO();
                    courseDTO.setCourseId(course.getCourseId());
                    courseDTO.setCourseName(course.getCourseName());
                    courseDTO.setCourseDescription(course.getCourseDescription());
                    courseDTO.setImage(course.getImage());

                    // Map each class under this course to StudentClassDTO
                    List<StudentClassDTO> classDTOs = classes.stream()
                            .map(classEntity -> {
                                StudentClassDTO classDTO = new StudentClassDTO();
                                classDTO.setClassId(classEntity.getClassesId());
                                classDTO.setClassName(classEntity.getClassName());
                                classDTO.setClassDescription(classEntity.getClassDescription());
                                classDTO.setStartDate(classEntity.getStartDate());
                                classDTO.setEndDate(classEntity.getEndDate());
                                return classDTO;
                            })
                            .collect(Collectors.toList());

                    courseDTO.setClasses(classDTOs);
                    return courseDTO;
                })
                .collect(Collectors.toList());

        studentDTO.setCourses(courseDTOs);
        return studentDTO;
    }
}
