package org.backend.examprep_backend.dto;

import java.util.List;

public class QuestionDTO {
    private String questionText;
    private List<AnswerDTO> answers;

    // Getters and Setters
    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public List<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDTO> answers) {
        this.answers = answers;
    }
}
