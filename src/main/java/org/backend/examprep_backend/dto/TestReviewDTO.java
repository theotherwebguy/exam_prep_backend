package org.backend.examprep_backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class TestReviewDTO {
    private Long questionId;
    private String questionText;
    private List<AnswerDTO> answers; // List of all possible answers for the question
    private Long selectedAnswerId;   // ID of the answer selected by the student
    private Long correctAnswerId;    // ID of the correct answer
    private boolean isCorrect;       // Whether the selected answer was correct
}
