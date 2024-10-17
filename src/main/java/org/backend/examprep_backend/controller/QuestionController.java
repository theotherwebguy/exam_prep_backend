package org.backend.examprep_backend.controller;

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
    public ResponseEntity<String> addQuestion(@RequestBody QuestionDTO questionDTO) {
        questionService.addQuestion(questionDTO);
        return ResponseEntity.ok("Question added successfully");
    }
}
