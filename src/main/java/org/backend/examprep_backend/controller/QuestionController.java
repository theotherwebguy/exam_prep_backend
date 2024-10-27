package org.backend.examprep_backend.controller;

import org.backend.examprep_backend.dto.AnswerDTO;
import org.backend.examprep_backend.dto.QuestionDTO;
import org.backend.examprep_backend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;
//
//    @PostMapping("/add")
//    public ResponseEntity<String> addQuestion(@RequestBody QuestionDTO questionDTO) {
//        questionService.addQuestion(questionDTO);
//        return ResponseEntity.ok("Question added successfully");
//    }

    // Endpoint to add a question with optional PDF file
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addQuestion(
            @RequestParam("questionText") String questionText,
            @RequestParam("topicId") Long topicId,
            @RequestParam("courseId") Long courseId,
            //@RequestParam("answerDescription") String answerDescription,
            @RequestParam("answers") List<AnswerDTO> answers,
            @RequestPart(value = "pdfFile", required = false) MultipartFile pdfFile) {

        try {
            // Populate QuestionDTO and call the service
            QuestionDTO questionDTO = new QuestionDTO();
            questionDTO.setQuestionText(questionText);
            questionDTO.setTopicId(topicId);
            questionDTO.setCourseId(courseId);
            questionDTO.setAnswers(answers);

            questionService.saveQuestionWithAnswersAndPdf(questionDTO, pdfFile);

            return ResponseEntity.status(HttpStatus.CREATED).body("Question added successfully.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to save question with PDF: " + e.getMessage());
        }
    }

}
