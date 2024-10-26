package org.backend.examprep_backend.service;

import org.backend.examprep_backend.dto.AnswerDTO;
import org.backend.examprep_backend.dto.QuestionDTO;
import org.backend.examprep_backend.model.*;
import org.backend.examprep_backend.repository.CourseRepository;
import org.backend.examprep_backend.repository.DomainRepository;
import org.backend.examprep_backend.repository.TopicRepository;
import org.backend.examprep_backend.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private DomainRepository domainRepository;

    @Autowired
    private CourseRepository courseRepository;

    public void addQuestion(QuestionDTO questionDTO) throws Exception {
        Optional<Course> optionalCourse = courseRepository.findById(questionDTO.getCourseId());
        if (optionalCourse.isEmpty()) {
            throw new RuntimeException("Course not found");
        }

        Optional<Topic> optionalTopic = topicRepository.findById(questionDTO.getTopicId());
        if (optionalTopic.isEmpty()) {
            throw new RuntimeException("Topic not found under this domain");
        }

        Topic topic = optionalTopic.get();
        Question question = new Question();
        question.setQuestionText(questionDTO.getQuestionText());
        question.setTopic(topic);
        question.setQuestionType(questionDTO.getQuestionType());
        question.setInstruction(questionDTO.getInstruction());

        // Handle different question types
        if ("MULTIPLE_CHOICE".equalsIgnoreCase(questionDTO.getQuestionType())) {
            List<Answer> answers = new ArrayList<>();
            for (AnswerDTO answerDTO : questionDTO.getAnswers()) {
                Answer answer = new Answer();
                answer.setAnswerText(answerDTO.getAnswerText());
                answer.setCorrect(answerDTO.isCorrect());
                answer.setQuestion(question);
                answers.add(answer);
            }
            question.setAnswers(answers);
        } else if ("TRUE_FALSE".equalsIgnoreCase(questionDTO.getQuestionType())) {
            Answer trueFalseAnswer = new Answer();
            trueFalseAnswer.setCorrect(questionDTO.getCorrect());
            trueFalseAnswer.setQuestion(question);
            question.setAnswers(List.of(trueFalseAnswer));
        } else if ("SCENARIO".equalsIgnoreCase(questionDTO.getQuestionType()) || "IMAGE_BASED".equalsIgnoreCase(questionDTO.getQuestionType())) {
            if (questionDTO.getPdfFile() != null) {
                String pdfPath = savePdfFile(questionDTO.getPdfFile());  // Save PDF and get path
                question.setPdfUrl(pdfPath);
            } else {
                throw new RuntimeException("PDF file is required for scenario/image-based questions.");
            }
        }

        questionRepository.save(question);
    }

    private String savePdfFile(MultipartFile file) throws Exception {
        Path path = Paths.get("uploads/" + file.getOriginalFilename());  // Set a path to save files
        Files.write(path, file.getBytes());  // Write file to path
        return path.toString();  // Return path as a string
    }
}
