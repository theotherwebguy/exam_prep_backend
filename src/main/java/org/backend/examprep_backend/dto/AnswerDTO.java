package org.backend.examprep_backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class AnswerDTO {
    // Getters and Setters

    private String answerText;

    private String answerDescription;
    private boolean isCorrect;

    public boolean isCorrect() {
        return isCorrect;
    }

}
