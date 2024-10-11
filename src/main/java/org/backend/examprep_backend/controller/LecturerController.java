package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Users;
import org.backend.examprep_backend.service.ClassesService;
import org.backend.examprep_backend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/lecturer")
public class LecturerController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private ClassesService classesService;

    // API to get courses assigned to the lecturer
    @GetMapping("/courses")
    public ResponseEntity<List<Course>> getCourses(Authentication authentication) {
        // Assume authentication object gives us the logged-in lecturer
        Users lecturer = (Users) authentication.getPrincipal();
        List<Course> courses = courseService.getCoursesByLecturer(lecturer);
        return ResponseEntity.ok(courses);
    }

    // Add a new class for a course
    @PostMapping("/courses/{courseId}/classes")
    public ResponseEntity<Classes> addClass(@PathVariable Long courseId, @RequestBody Classes newClass) {
        Course course = courseService.getCourseById(courseId);
        newClass.setCourse(course);
        Classes createdClass = classesService.addClass(newClass);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdClass);
    }

    // Update a class
    @PutMapping("/classes/{classId}")
    public ResponseEntity<Classes> updateClass(@PathVariable Long classId, @RequestBody Classes updatedClass) {
        Classes updated = classesService.updateClass(classId, updatedClass);
        return ResponseEntity.ok(updated);
    }

    // Delete a class
    @DeleteMapping("/classes/{classId}")
    public ResponseEntity<Void> deleteClass(@PathVariable Long classId) {
        classesService.deleteClass(classId);
        return ResponseEntity.noContent().build();
    }
}