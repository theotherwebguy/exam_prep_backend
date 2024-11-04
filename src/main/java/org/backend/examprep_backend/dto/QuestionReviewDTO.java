package org.backend.examprep_backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class QuestionReviewDTO {
    private Long questionId;
    private String questionText;
    private Long studentAnswerId;
    private Long correctAnswerId;
}
