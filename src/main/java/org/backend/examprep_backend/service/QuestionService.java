package org.backend.examprep_backend.service;

import org.backend.examprep_backend.dto.AnswerDTO;
import org.backend.examprep_backend.dto.QuestionDTO;
import org.backend.examprep_backend.model.Answer;
import org.backend.examprep_backend.model.Question;
import org.backend.examprep_backend.model.Topic;
import org.backend.examprep_backend.repository.QuestionRepository;
import org.backend.examprep_backend.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Transactional // Ensure atomic operations
    public void saveQuestionWithPdf(QuestionDTO questionDTO, MultipartFile pdfFile) throws IOException {
        Question question = new Question();
        question.setQuestionText(questionDTO.getQuestionText());
        question.setQuestionType(questionDTO.getQuestionType());
        question.setInstruction(questionDTO.getInstruction());
        question.setModerated(false);// Always false when created

        Topic topic = topicRepository.findById(questionDTO.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        question.setTopic(topic);

        if (pdfFile != null && !pdfFile.isEmpty()) {
            validatePdfFile(pdfFile); // Ensure it’s a valid PDF
            String pdfFilePath = saveFileToLocalDirectory(pdfFile);
            question.setPdfFileUrl(pdfFilePath);
        }

        List<Answer> answers = questionDTO.getAnswers().stream()
                .map(answerDTO -> {
                    Answer answer = new Answer();
                    answer.setAnswerText(answerDTO.getAnswerText());
                    answer.setAnswerDescription(answerDTO.getAnswerDescription());
                    answer.setCorrect(answerDTO.getIsCorrect());
                    answer.setQuestion(question);
                    return answer;
                }).collect(Collectors.toList());

        question.setAnswers(answers);
        questionRepository.save(question);
    }

    private String saveFileToLocalDirectory(MultipartFile file) throws IOException {
        Path directoryPath = Paths.get(uploadDir);
        if (Files.notExists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = directoryPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath.toString();
    }

    private void validatePdfFile(MultipartFile file) {
        if (!file.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("Uploaded file must be a PDF.");
        }
    }


    @Transactional
    public List<Question> getQuestionsByTopicId(Long topicId) {
        return questionRepository.findByTopic_TopicId(topicId); // Assuming you have a repository method for this
    }

    @Transactional
    public List<QuestionDTO> getUnmoderatedQuestionsByCourseId(Long courseId) {
        List<Question> questions = questionRepository.findUnmoderatedQuestionsByCourseId(courseId);
        return questions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuestionDTO convertToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setQuestionId(question.getQuestionId());
        dto.setQuestionText(question.getQuestionText());
        dto.setTopicId(question.getTopic().getTopicId());

        // Assuming question -> topic -> domain -> course relationship
        Long courseId = question.getTopic().getDomain().getCourse().getCourseId();
        dto.setCourseId(courseId); // Set the courseId in the DTO


        dto.setQuestionType(question.getQuestionType());
        dto.setInstruction(question.getInstruction());
        dto.setPdfFileUrl(question.getPdfFileUrl());

        dto.setAnswers(question.getAnswers().stream()
                .map(answer -> {
                    AnswerDTO answerDTO = new AnswerDTO();
                    answerDTO.setAnswerId(answer.getAnswerId());
                    answerDTO.setAnswerText(answer.getAnswerText());
                    answerDTO.setAnswerDescription(answer.getAnswerDescription());
                    answerDTO.setIsCorrect(answer.isCorrect());
                    return answerDTO;
                })
                .collect(Collectors.toList()));

        return dto;
    }

    @Transactional
    public void updateQuestionWithPdf(Long questionId, QuestionDTO questionDTO, MultipartFile pdfFile) throws IOException {
        // Find the existing question
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionId));

        // Update question properties
        question.setQuestionText(questionDTO.getQuestionText());
        question.setQuestionType(questionDTO.getQuestionType());
        question.setInstruction(questionDTO.getInstruction());
        question.setModerated(true); // Set to true when updated by a moderator

        // Update topic if provided
        Topic topic = topicRepository.findById(questionDTO.getTopicId())
                .orElseThrow(() -> new RuntimeException("Topic not found"));
        question.setTopic(topic);

        // If a new PDF file is provided, replace the existing one
        if (pdfFile != null && !pdfFile.isEmpty()) {
            validatePdfFile(pdfFile); // Ensure it’s a valid PDF
            String pdfFilePath = saveFileToLocalDirectory(pdfFile);
            question.setPdfFileUrl(pdfFilePath);
        }

        // Map existing answers by their IDs for easy lookup
        Map<Long, Answer> existingAnswersMap = question.getAnswers().stream()
                .collect(Collectors.toMap(Answer::getAnswerId, answer -> answer));

        // Update existing answers or add new ones
        for (AnswerDTO answerDTO : questionDTO.getAnswers()) {

            if (answerDTO.getAnswerId() != null && existingAnswersMap.containsKey(answerDTO.getAnswerId())) {
                // Update the existing answer

                Answer answer = existingAnswersMap.get(answerDTO.getAnswerId());
                answer.setAnswerText(answerDTO.getAnswerText());
                answer.setAnswerDescription(answerDTO.getAnswerDescription());
                answer.setCorrect(answerDTO.getIsCorrect());
            } else {
                // Create new answer if it doesn't exist
                Answer newAnswer = new Answer();
                newAnswer.setAnswerText(answerDTO.getAnswerText());
                newAnswer.setAnswerDescription(answerDTO.getAnswerDescription());
                newAnswer.setCorrect(answerDTO.getIsCorrect());
                newAnswer.setQuestion(question);
                question.getAnswers().add(newAnswer); // Add to question’s answer list
            }
        }
        // Save the updated question
        questionRepository.save(question);
    }


    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionId));
    }

}
