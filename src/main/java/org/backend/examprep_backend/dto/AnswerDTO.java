package org.backend.examprep_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AnswerDTO {

    private String answerText;
    private Boolean isCorrect;
    private String answerDescription;
}
