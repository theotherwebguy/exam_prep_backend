package org.backend.examprep_backend.dto;

import lombok.Getter;
import lombok.Setter;

public class AnswerDTO {
    // Getters and Setters
    @Getter
    @Setter
    private String answerText;
    @Setter
    @Getter
    private String answerDescription;
    private boolean isCorrect;

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
