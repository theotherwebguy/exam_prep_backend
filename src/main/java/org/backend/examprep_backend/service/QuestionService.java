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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public void addQuestion(QuestionDTO questionDTO) {
        // Validate the course
        Optional<Course> optionalCourse = courseRepository.findById(questionDTO.getCourseId());
        if (optionalCourse.isEmpty()) {
            throw new RuntimeException("Course not found");
        }

        // Validate the domain
        Optional<Domain> optionalDomain = domainRepository.findById(questionDTO.getDomainId());
        if (optionalDomain.isEmpty()) {
            throw new RuntimeException("Domain not found under this course");
        }

        // Validate the topic
        Optional<Topic> optionalTopic = topicRepository.findById(questionDTO.getTopicId());
        if (optionalTopic.isEmpty()) {
            throw new RuntimeException("Topic not found under this domain");
        }

        Topic topic = optionalTopic.get();

        // Create a new Question and associate it with the topic
        Question question = new Question();
        question.setQuestionText(questionDTO.getQuestionText());
        question.setTopic(topic);

        // Add answers
        List<Answer> answers = new ArrayList<>();
        for (AnswerDTO answerDTO : questionDTO.getAnswers()) {
            Answer answer = new Answer();
            answer.setAnswerText(answerDTO.getAnswerText());
            answer.setCorrect(answerDTO.isCorrect());
            answer.setAnswerDescription(questionDTO.getAnswerDescription());
            answer.setQuestion(question);
            answers.add(answer);
        }

        question.setAnswers(answers);
        questionRepository.save(question);
    }

    public void processDump(MultipartFile file) throws Exception {
        List<String> lines = new BufferedReader(new InputStreamReader(file.getInputStream()))
                .lines()
                .collect(Collectors.toList());

        Question currentQuestion = null;
        List<Answer> answers = new ArrayList<>();

        for (String line : lines) {
            if (line.startsWith("Q:")) { // New question
                if (currentQuestion != null) {
                    currentQuestion.setAnswers(answers);
                    questionRepository.save(currentQuestion); // Save the previous question
                }

                answers = new ArrayList<>();
                String questionText = line.substring(2).trim();
                currentQuestion = new Question();
                currentQuestion.setQuestionText(questionText);

            } else if (line.startsWith("A:")) { // New answer
                String[] answerParts = line.substring(2).split(",");
                if (answerParts.length < 2) continue; // Skip malformed lines

                String answerText = answerParts[0].trim();
                boolean isCorrect = Boolean.parseBoolean(answerParts[1].trim());

                Answer answer = new Answer();
                answer.setAnswerText(answerText);
                answer.setCorrect(isCorrect);
                answer.setQuestion(currentQuestion); // Link answer to the current question
                answers.add(answer);
            }
        }

        // Save the last question if it exists
        if (currentQuestion != null) {
            currentQuestion.setAnswers(answers);
            questionRepository.save(currentQuestion);
        }
    }
}
