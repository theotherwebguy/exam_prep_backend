package org.backend.examprep_backend.dto;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@Data
public class QuestionDTO {

    private Long courseId;

    private Long topicId;
    private String questionText;
    private String questionType;// e.g., "MULTIPLE_CHOICE", "TRUE_FALSE", "SCENARIO", etc.
    private String answerDescription;

    private MultipartFile pdfFile; // For scenario or image-based questions
    private String instruction; // Custom instruction for the user
    private List<AnswerDTO> answers;
    private Boolean correct;  // For true/false questions
}
