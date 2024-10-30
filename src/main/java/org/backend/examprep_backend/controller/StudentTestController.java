package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.CourseDTO;
import org.backend.examprep_backend.dto.IndependentTestDTO;
import org.backend.examprep_backend.dto.TestReviewDTO;
import org.backend.examprep_backend.service.IndependentTestService;
import org.backend.examprep_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tests")
public class StudentTestController {

    @Autowired
    private IndependentTestService testService;

    @Autowired
    private UserService userService;

    // Endpoint to create a test using DTO and fetching Domain, Topics, and Questions
    @PostMapping("/CreateTests")
    public ResponseEntity<IndependentTestDTO> createTest(@RequestBody IndependentTestDTO testDTO, @RequestParam Long studentId) {
        // Use IndependentTestDTO as the return type, not the entity Test
        IndependentTestDTO createdTestDTO = testService.createTestWithDetails(testDTO, studentId);

        return new ResponseEntity<>(createdTestDTO, HttpStatus.CREATED);
    }

    @GetMapping("/reviews/{studentId}")
    public ResponseEntity<List<TestReviewDTO>> getReviewsByStudentId(@PathVariable Long studentId) {
        List<TestReviewDTO> reviews = testService.findByStudentId(studentId);
        return ResponseEntity.ok(reviews);
    }

    // Fetch courses by independent student ID
    @GetMapping("/{studentId}/courses")
    @Transactional
    public ResponseEntity<List<CourseDTO>> getCoursesByUserId(@PathVariable Long studentId) {
        List<CourseDTO> courses = testService.getCoursesByUserId(studentId);

        // Map to CourseDTO
        List<CourseDTO> courseDTOList = courses.stream().map(course -> {
            CourseDTO courseDTO = new CourseDTO();
            courseDTO.setCourseId(course.getCourseId());
            courseDTO.setCourseName(course.getCourseName());
            courseDTO.setCourseDescription(course.getCourseDescription());
            courseDTO.setDomains(course.getDomains()); // Now includes domains and topics
            courseDTO.setImage(course.getImage()); // Always include image

            return courseDTO;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(courseDTOList);
    }
}
