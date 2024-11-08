package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.*;
import org.backend.examprep_backend.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class TestController {

    @Autowired
    private TestService testService;
    // Create a new test
    @PostMapping
    public ResponseEntity<TestDTO> createTest(@RequestBody TestCreationRequestDTO request,
                                              @RequestParam Long studentId) {
        TestDTO createdTest = testService.createTest(request, studentId);
        return new ResponseEntity<>(createdTest, HttpStatus.CREATED);
    }


    // Start a test by fetching questions for the student
    @GetMapping("/{testId}/start")
    public ResponseEntity<TestAttemptDTO> startTest(@PathVariable Long testId,
                                                    @RequestParam Long studentId) {
        TestAttemptDTO testAttemptDTO = testService.startTest(testId, studentId);
        return ResponseEntity.ok(testAttemptDTO);
    }
    // Submit answers for a test attempt
    @PostMapping("/{testAttemptId}/submit")
    public ResponseEntity<String> submitAnswers(
            @PathVariable Long testAttemptId,
            @RequestBody List<TestAttemptAnswerDTO> answers) {
        try {
            testService.submitAnswers(testAttemptId, answers);  // Submit the answers via the service method
            return ResponseEntity.ok("Test submitted successfully");  // Respond with success message
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)  // Handle any errors
                    .body("Error while submitting test: " + e.getMessage());
        }
    }


    @GetMapping("/{testAttemptId}/review")
    public ResponseEntity<List<TestReviewDTO>> reviewTest(@PathVariable Long testAttemptId) {
        try {
            List<TestReviewDTO> review = testService.reviewTest(testAttemptId);
            return ResponseEntity.ok(review);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList());
        }
    }

    @PostMapping("/create-by-lecturer")
    public ResponseEntity<TestDTO> createLecturerTest(@RequestBody LecturerTestCreationRequestDTO request) {
        TestDTO createdTest = testService.createLecturerTest(request);
        return new ResponseEntity<>(createdTest, HttpStatus.CREATED);
    }
}
