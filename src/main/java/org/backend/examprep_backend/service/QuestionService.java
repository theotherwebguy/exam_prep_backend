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

    public void addQuestion(QuestionDTO questionDTO) {
        // Validate the course
        Optional<Course> optionalCourse = courseRepository.findById(questionDTO.getCourseId());
        if (optionalCourse.isEmpty()) {
            throw new RuntimeException("Course not found");
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
}
