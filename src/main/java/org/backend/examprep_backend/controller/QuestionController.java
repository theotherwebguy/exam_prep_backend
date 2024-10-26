package org.backend.examprep_backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.backend.examprep_backend.dto.QuestionDTO;
import org.backend.examprep_backend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping("/add")
    @Operation(summary = "Add a new question", description = "Adds a new question of various types. Supports PDF upload for scenario and image-based questions.")
    @ApiResponse(responseCode = "200", description = "Question added successfully")
    public ResponseEntity<String> addQuestion(@ModelAttribute QuestionDTO questionDTO) throws Exception {
        questionService.addQuestion(questionDTO);
        return ResponseEntity.ok("Question added successfully");
    }

}
