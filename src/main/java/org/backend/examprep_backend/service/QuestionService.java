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
import java.util.List;
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

//    @Transactional
//    public List<QuestionDTO> getQuestionsByTopicId(Long topicId) {
//        List<Question> questions = questionRepository.findByTopic_TopicId(topicId);
//        return questions.stream().map(this::convertToDTO).collect(Collectors.toList());
//    }
//
//    private QuestionDTO convertToDTO(Question question) {
//        QuestionDTO dto = new QuestionDTO();
//        dto.setQuestionText(question.getQuestionText());
//        dto.setTopicId(question.getTopic().getTopicId());
//        dto.setQuestionType(question.getQuestionType());
//        dto.setInstruction(question.getInstruction());
//
//
//
//        dto.setPdfFile(null); // Handle PDF as needed
//        dto.setAnswers(question.getAnswers().stream()
//                .map(answer -> new AnswerDTO(answer.getAnswerText(), answer.isCorrect(), answer.getAnswerDescription()))
//                .collect(Collectors.toList()));
//        return dto;
//    }

    @Transactional
    public List<Question> getQuestionsByTopicId(Long topicId) {
        return questionRepository.findByTopic_TopicId(topicId); // Assuming you have a repository method for this
    }

    public List<QuestionDTO> getUnmoderatedQuestionsByCourseId(Long courseId) {
        List<Question> questions = questionRepository.findUnmoderatedQuestionsByCourseId(courseId);
        return questions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public QuestionDTO convertToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setQuestionText(question.getQuestionText());
        dto.setTopicId(question.getTopic().getTopicId());
        dto.setQuestionType(question.getQuestionType());
        dto.setInstruction(question.getInstruction());
        dto.setPdfFileUrl(question.getPdfFileUrl());

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

        // Update answers: Clear existing answers and add the new ones
        question.getAnswers().clear();  // Clear the existing list without replacing it
        List<Answer> newAnswers = questionDTO.getAnswers().stream()
                .map(answerDTO -> {
                    Answer answer = new Answer();
                    answer.setAnswerText(answerDTO.getAnswerText());
                    answer.setAnswerDescription(answerDTO.getAnswerDescription());
                    answer.setCorrect(answerDTO.getIsCorrect());
                    answer.setQuestion(question);
                    return answer;
                }).collect(Collectors.toList());

        question.getAnswers().addAll(newAnswers); // Add all new answers to the existing list

        // Save the updated question
        questionRepository.save(question);
    }

    public Question getQuestionById(Long questionId) {
        return questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + questionId));
    }

}
