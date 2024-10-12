package org.backend.examprep_backend.service;

import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CourseService {
    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseById(Long courseId) {
        return courseRepository.findById(courseId);
    }

    public Course addCourse(Course course) {
        return courseRepository.save(course);
    }

    public Course updateCourse(Long courseId, Course updatedCourse) {
        return courseRepository.findById(courseId).map(course -> {
            course.setCourseName(updatedCourse.getCourseName());
            course.setCourseDescription(updatedCourse.getCourseDescription());
            course.setImage(updatedCourse.getImage());
            return courseRepository.save(course);
        }).orElseThrow(() -> new RuntimeException("Course not found"));
    }

    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }
}
