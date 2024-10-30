package org.backend.examprep_backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.backend.examprep_backend.model.QuestionType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class QuestionDTO {
    private Long courseId;
    private Long questionId;
    private String questionText;
    private Long topicId;
    private String topicName; // The name of the associated topic
    private Long domainId;
    private String domainName; // The name of the associated domain
    private QuestionType questionType;
    private List<AnswerDTO> answers;
    private String instruction;
//    private MultipartFile pdfFile;  // PDF for scenario-based or image-heavy questions
    private String pdfFileUrl;
//    private boolean isModerated;
}

