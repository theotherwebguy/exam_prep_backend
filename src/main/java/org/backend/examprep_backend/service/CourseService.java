package org.backend.examprep_backend.service;

import org.backend.examprep_backend.ResourceNotFoundException;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    // Retrieve courses by lecturer
    public List<Course> getCoursesByLecturer(Users lecturer) {
        return courseRepository.findByLecturer(lecturer);
    }
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));
    }
}
