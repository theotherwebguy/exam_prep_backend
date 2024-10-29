package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.CourseDTO;
import org.backend.examprep_backend.dto.EnrolledTestDTO;
import org.backend.examprep_backend.service.EnrolledTestService;
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
public class EnrolledTestController {

    @Autowired
    private EnrolledTestService testService;

    @Autowired
    private UserService userService;
    @Autowired
    private EnrolledTestService independentTestService;

    // Endpoint to create a test using DTO and fetching Domain, Topics, and Questions
    @PostMapping("enrolled/CreateTests")
    public ResponseEntity<EnrolledTestDTO> createTest(@RequestBody EnrolledTestDTO testDTO) {
        // Use IndependentTestDTO as the return type, not the entity Test
        EnrolledTestDTO createdTestDTO = testService.createTestWithDetails(testDTO);

        return new ResponseEntity<>(createdTestDTO, HttpStatus.CREATED);
    }

    // Endpoint to get a test by ID (returning DTO)
//    @GetMapping("/{testId}")
//    public ResponseEntity<IndependentTestDTO> getTestById(@PathVariable Long testId) {
//        return testService.getTestById(testId)
//                .map(testDTO -> new ResponseEntity<>(testDTO, HttpStatus.OK))
//                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }

    // Endpoint to get all tests
//    @GetMapping("/GetAllTests")
//    public List<IndependentTestDTO> getAllTests() {
//        return testService.getAllTests();
//    }



    //fetch course by independent student id

    @GetMapping("enrolled/{studentId}/courses")
    @Transactional
    public ResponseEntity<List<CourseDTO>> getCoursesByUserId(
            @PathVariable Long studentId) { // Removed the includeImage parameter

        List<CourseDTO> courses = independentTestService.getCoursesByUserId(studentId);

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

