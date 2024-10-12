package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.CourseDTO;
import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public List<CourseDTO> getAllCourses() {
        return courseService.getAllCourses().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long courseId) {
        return courseService.getCourseById(courseId)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CourseDTO> addCourse(@RequestBody CourseDTO courseDTO) {
        Course course = convertToEntity(courseDTO);
        Course savedCourse = courseService.addCourse(course);
        return ResponseEntity.ok(convertToDTO(savedCourse));
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long courseId, @RequestBody CourseDTO updatedCourseDTO) {
        Course updatedCourse = convertToEntity(updatedCourseDTO);
        return ResponseEntity.ok(convertToDTO(courseService.updateCourse(courseId, updatedCourse)));
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    // Convert Course entity to CourseDTO
    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setCourseId(course.getCourseId());
        dto.setCourseName(course.getCourseName());
        dto.setCourseDescription(course.getCourseDescription());
        dto.setImage(course.getImage());
        return dto;
    }

    // Convert CourseDTO to Course entity
    private Course convertToEntity(CourseDTO dto) {
        Course course = new Course();
        course.setCourseName(dto.getCourseName());
        course.setCourseDescription(dto.getCourseDescription());
        course.setImage(dto.getImage());
        return course;
    }
}