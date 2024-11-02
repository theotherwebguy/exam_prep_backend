package org.backend.examprep_backend.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class AnswerDTO {

    private Long answerId;
    private String answerText;
    private Boolean isCorrect;
    private String answerDescription;

}
