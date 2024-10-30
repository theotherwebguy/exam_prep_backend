package org.backend.examprep_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.backend.examprep_backend.dto.AnswerDTO;
import org.backend.examprep_backend.dto.QuestionDTO;
import org.backend.examprep_backend.model.Domain;
import org.backend.examprep_backend.model.Question;
import org.backend.examprep_backend.model.Topic;
import org.backend.examprep_backend.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
        dto.setQuestionId(question.getQuestionId());
        dto.setQuestionText(question.getQuestionText());
        dto.setTopicId(question.getTopic().getTopicId());
        dto.setQuestionType(question.getQuestionType());
        dto.setInstruction(question.getInstruction());
        dto.setPdfFileUrl(question.getPdfFileUrl()); // Add this if you modify QuestionDTO to include i

       //set Topic and Domain information
        Topic topic = question.getTopic();
        dto.setTopicId(topic.getTopicId());
        dto.setTopicName(topic.getTopicName());

        Domain domain = topic.getDomain();
        if(domain!=null){
            dto.setDomainId(domain.getDomainId());
            dto.setDomainName(domain.getDomainName());
        }


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


    @Transactional
    @GetMapping("/unmoderated/{courseId}")
    public ResponseEntity<List<QuestionDTO>> getUnmoderatedQuestionsByCourseId(@PathVariable Long courseId) {
        List<QuestionDTO> questionDTOs = questionService.getUnmoderatedQuestionsByCourseId(courseId);
        return ResponseEntity.ok(questionDTOs);
    }

    @PutMapping(value = "/update/{questionId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateQuestion(
            @PathVariable Long questionId,
            @RequestPart("questionDTO") String questionDTOJson,
            @RequestPart(value = "pdfFile", required = false) MultipartFile pdfFile) {

        try {
            // Convert JSON string to QuestionDTO object
            ObjectMapper objectMapper = new ObjectMapper();
            QuestionDTO questionDTO = objectMapper.readValue(questionDTOJson, QuestionDTO.class);

            // Pass questionId, questionDTO, and pdfFile to the service
            questionService.updateQuestionWithPdf(questionId, questionDTO, pdfFile);
            return ResponseEntity.ok("Question updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while updating question: " + e.getMessage());
        }
    }


    @GetMapping("/question/{questionId}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable Long questionId) {
        Question question = questionService.getQuestionById(questionId); // Modify the service method to fetch by questionId
        QuestionDTO questionDTO = convertToDTO(question);
        return ResponseEntity.ok(questionDTO);
    }

}
