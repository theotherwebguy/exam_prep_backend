package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.TestDTO;
import org.backend.examprep_backend.model.Test;
import org.backend.examprep_backend.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tests")
public class TestController {

    private final TestService testService;

    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }

    @PostMapping("/create")
    public ResponseEntity<Test> createTest(@RequestBody TestDTO testDTO) {
        Test createdTest = testService.createTestWithDetails(testDTO);
        return ResponseEntity.ok(createdTest);
    }
}
