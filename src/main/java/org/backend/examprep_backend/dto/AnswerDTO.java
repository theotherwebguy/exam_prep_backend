package org.backend.examprep_backend.dto;

import lombok.Data;

@Data
public class AnswerDTO {

    private String answerText;
    private Boolean isCorrect;
    private String answerDescription;
}
