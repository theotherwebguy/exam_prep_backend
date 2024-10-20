package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.QuestionDTO;
import org.backend.examprep_backend.service.QuestionService;
import org.backend.examprep_backend.service.DumpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private DumpService dumpService; // Inject DumpService

    // Endpoint to add a single question
    @PostMapping("/add")
    public ResponseEntity<String> addQuestion(@RequestBody QuestionDTO questionDTO) {
        questionService.addQuestion(questionDTO);
        return ResponseEntity.ok("Question added successfully");
    }

    // Endpoint to upload a dump file and extract questions/answers
    @PostMapping(value = "/upload-dump", consumes = "multipart/form-data")
    public ResponseEntity<String> uploadDump(@RequestParam("file") MultipartFile file) {
        try {
            dumpService.processDump(file);
            return ResponseEntity.ok("Dump uploaded and processed successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing dump: " + e.getMessage());
        }
    }
}
