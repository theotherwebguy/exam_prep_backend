package org.backend.examprep_backend.service;

import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.ClassesRepository;
import org.backend.examprep_backend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ClassesRepository classesRepository;

    // Retrieve courses that have classes assigned to the lecturer
    public List<Course> getCoursesWithClassesByLecturer(Users lecturer) {
        List<Classes> classesList = classesRepository.findByLecturer(lecturer);
        return classesList.stream()
                .map(Classes::getCourse)
                .distinct() // Avoid duplicate courses
                .collect(Collectors.toList());
    }

    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
    }
}

