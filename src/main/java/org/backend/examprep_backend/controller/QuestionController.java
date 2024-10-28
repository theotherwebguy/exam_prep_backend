package org.backend.examprep_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.backend.examprep_backend.dto.AnswerDTO;
import org.backend.examprep_backend.dto.QuestionDTO;
import org.backend.examprep_backend.model.Question;
import org.backend.examprep_backend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

//    @GetMapping("/topic/{topicId}")
//    public ResponseEntity<List<QuestionDTO>> getQuestionsByTopicId(@PathVariable Long topicId) {
//        List<QuestionDTO> questions = questionService.getQuestionsByTopicId(topicId);
//        if (questions.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(questions);
//    }

    @GetMapping("/{topicId}") // Adjust the endpoint to include topicId
    public ResponseEntity<List<QuestionDTO>> getQuestionsByTopic(@PathVariable Long topicId) {
        List<Question> questions = questionService.getQuestionsByTopicId(topicId); // Modify the service method to fetch by topicId
        List<QuestionDTO> questionDTOs = questions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(questionDTOs);
    }

    private QuestionDTO convertToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setQuestionText(question.getQuestionText());
        dto.setTopicId(question.getTopic().getTopicId());
        dto.setQuestionType(question.getQuestionType());
        dto.setInstruction(question.getInstruction());
        dto.setPdfFileUrl(question.getPdfFileUrl()); // Add this if you modify QuestionDTO to include i
        // Handle answers if needed
        dto.setAnswers(question.getAnswers().stream()
                .map(answer -> {
                    AnswerDTO answerDTO = new AnswerDTO();
                    answerDTO.setAnswerText(answer.getAnswerText());
                    answerDTO.setAnswerDescription(answer.getAnswerDescription());
                    answerDTO.setIsCorrect(answer.isCorrect());
                    return answerDTO;
                })
                .collect(Collectors.toList()));
        return dto;
    }

}
