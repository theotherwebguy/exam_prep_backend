package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.TestCreationRequestDTO;
import org.backend.examprep_backend.dto.TestDTO;
import org.backend.examprep_backend.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
