package org.backend.examprep_backend.service;

import org.backend.examprep_backend.dto.AnswerDTO;
import org.backend.examprep_backend.dto.QuestionDTO;
import org.backend.examprep_backend.model.Answer;
import org.backend.examprep_backend.model.Question;
import org.backend.examprep_backend.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public void addQuestion(QuestionDTO questionDTO) {
        // Create a Question object
        Question question = new Question();
        question.setQuestionText(questionDTO.getQuestionText());

        // Create a list of answers
        List<Answer> answers = new ArrayList<>();
        for (AnswerDTO answerDTO : questionDTO.getAnswers()) {
            Answer answer = new Answer();
            answer.setAnswerText(answerDTO.getAnswerText());
            answer.setAnswerDescription(answerDTO.getAnswerDescription());
            answer.setCorrect(answerDTO.isCorrect());
            answer.setQuestion(question);
            answers.add(answer);
        }

        // Set answers to the question and save it
        question.setAnswers(answers);
        questionRepository.save(question);
    }
}
