package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.CourseDTO;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;
    @PostMapping
    public ResponseEntity<Course> addCourseWithDomainsAndTopics(@RequestBody CourseDTO courseDTO) {
        Course savedCourse = courseService.saveCourseWithDomainsAndTopics(courseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCourse);
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Course> updateCourse(@PathVariable Long courseId, @RequestBody CourseDTO courseDTO) {
        Course updatedCourse = courseService.updateCourseWithDomainsAndTopics(courseId, courseDTO);
        return ResponseEntity.ok(updatedCourse);
    }



    // New: Delete an existing course
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long courseId) {
        Course course = courseService.getCourseById(courseId);
        return ResponseEntity.ok(course);
    }

}
