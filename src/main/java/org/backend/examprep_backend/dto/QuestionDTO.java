package org.backend.examprep_backend.dto;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

public class QuestionDTO {

    @Getter @Setter
    private String questionText;

    @Getter @Setter
    private Long courseId;

    @Getter @Setter
    private Long domainId;

    @Getter @Setter
    private Long topicId;

    @Getter @Setter
    private String answerDescription;

    @Getter @Setter
    private List<AnswerDTO> answers;
}
