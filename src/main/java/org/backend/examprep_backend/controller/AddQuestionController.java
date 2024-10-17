package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.AddQuestionDTO;
import org.backend.examprep_backend.model.AddQuestion;
import org.backend.examprep_backend.service.AddQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/questions")
public class AddQuestionController {

    @Autowired
    private AddQuestionService questionService;

    @PostMapping
    public ResponseEntity<AddQuestion> addQuestion(@RequestBody AddQuestionDTO questionDTO) {
        AddQuestion createdQuestion = questionService.createQuestion(questionDTO);
        return ResponseEntity.ok(createdQuestion);
    }
}
