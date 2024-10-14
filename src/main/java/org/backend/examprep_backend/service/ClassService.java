package org.backend.examprep_backend.service;

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
    private RoleRepository roleRepository;

    public Classes addClass(Long courseId, ClassDTO classDTO) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        Users lecturer = userRepository.findById(classDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + classDTO.getUserId()));

        if (!lecturer.getRole().getName().equalsIgnoreCase("Lecturer")) {
            throw new InvalidRoleException("User is not a Lecturer");
        }
        Classes newClass = new Classes();
        newClass.setClassName(classDTO.getClassName());
        newClass.setClassDescription(classDTO.getClassDescription());
        newClass.setStartDate(classDTO.getStartDate());
        newClass.setEndDate(classDTO.getEndDate());
        newClass.setCourse(course);
        newClass.setLecturer(lecturer);

        return classRepository.save(newClass);
    }




}
