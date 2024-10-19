package org.backend.examprep_backend.dto;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

public class QuestionDTO {

    @Getter @Setter
    private String questionText;

    @Getter @Setter
    private Long topicId;  // Topic ID reference

    @Getter @Setter
    private List<AnswerDTO> answers;
}
