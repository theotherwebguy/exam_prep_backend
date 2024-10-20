package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.IndependentTestDTO;
import org.backend.examprep_backend.service.IndependentTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class IndependentTestController {

    @Autowired
    private IndependentTestService testService;

    // Endpoint to create a test using DTO and fetching Domain, Topics, and Questions
    @PostMapping("/CreateTests")
    public ResponseEntity<IndependentTestDTO> createTest(@RequestBody IndependentTestDTO testDTO) {
        // Use IndependentTestDTO as the return type, not the entity Test
        IndependentTestDTO createdTestDTO = testService.createTestWithDetails(testDTO);

        return new ResponseEntity<>(createdTestDTO, HttpStatus.CREATED);
    }

    // Endpoint to get a test by ID (returning DTO)
    @GetMapping("/{testId}")
    public ResponseEntity<IndependentTestDTO> getTestById(@PathVariable Long testId) {
        return testService.getTestById(testId)
                .map(testDTO -> new ResponseEntity<>(testDTO, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // Endpoint to get all tests
    @GetMapping("/GetAllTests")
    public List<IndependentTestDTO> getAllTests() {
        return testService.getAllTests();
    }
}
