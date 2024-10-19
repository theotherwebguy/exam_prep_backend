package org.backend.examprep_backend.service;

import org.backend.examprep_backend.dto.AnswerDTO;
import org.backend.examprep_backend.dto.QuestionDTO;
import org.backend.examprep_backend.model.Answer;
import org.backend.examprep_backend.model.Question;
import org.backend.examprep_backend.model.Topic;
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

    public void addQuestion(QuestionDTO questionDTO) {
        // Find the topic by ID
        Optional<Topic> optionalTopic = topicRepository.findById(questionDTO.getTopicId());
        if (optionalTopic.isEmpty()) {
            throw new RuntimeException("Topic not found");
        }
        Topic topic = optionalTopic.get();

        // Create a new Question and associate it with the topic
        Question question = new Question();
        question.setQuestionText(questionDTO.getQuestionText());
        question.setTopic(topic);  // Ensure this works

        // Create and associate the answers
        List<Answer> answers = new ArrayList<>();
        for (AnswerDTO answerDTO : questionDTO.getAnswers()) {
            Answer answer = new Answer();
            answer.setAnswerText(answerDTO.getAnswerText());
            answer.setAnswerDescription(answerDTO.getAnswerDescription());
            answer.setCorrect(answerDTO.isCorrect());
            answer.setQuestion(question);
            answers.add(answer);
        }

        // Save the question along with answers
        question.setAnswers(answers);
        questionRepository.save(question);
    }
}
