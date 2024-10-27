package org.backend.examprep_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.backend.examprep_backend.dto.QuestionDTO;
import org.backend.examprep_backend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @PostMapping(value = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> addQuestion(
            @RequestPart("questionDTO") String questionDTOJson,
            @RequestPart(value = "pdfFile", required = false) MultipartFile pdfFile) {

        try {
            // Convert JSON string to QuestionDTO object
            ObjectMapper objectMapper = new ObjectMapper();
            QuestionDTO questionDTO = objectMapper.readValue(questionDTOJson, QuestionDTO.class);

            // Pass the questionDTO and pdfFile to the service
            questionService.saveQuestionWithPdf(questionDTO, pdfFile);
            return ResponseEntity.status(HttpStatus.CREATED).body("Question added successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while saving question: " + e.getMessage());
        }
    }

}
