package org.backend.examprep_backend.service;

import org.backend.examprep_backend.dto.AddQuestionDTO;
import org.backend.examprep_backend.model.AddQuestion;
import org.backend.examprep_backend.repository.AddQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddQuestionService {

    @Autowired
    private AddQuestionRepository questionRepository;

    public AddQuestion createQuestion(AddQuestionDTO dto) {
        AddQuestion question = new AddQuestion();
        question.setDomain(dto.getDomain());
        question.setQuestionText(dto.getQuestionText());
        question.setCorrectAnswers(dto.getCorrectAnswers());
        question.setIncorrectAnswers(dto.getIncorrectAnswers());
        question.setCorrectAnswerDescription(dto.getCorrectAnswerDescription());

        return questionRepository.save(question);
    }
}
