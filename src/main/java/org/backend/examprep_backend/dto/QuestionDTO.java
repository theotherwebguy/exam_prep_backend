package org.backend.examprep_backend.dto;

import lombok.Data;
import org.backend.examprep_backend.model.QuestionType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class QuestionDTO {
    private String questionText;
    private Long topicId;
    private QuestionType questionType;
    private List<AnswerDTO> answers;
    private String instruction;
    private MultipartFile pdfFile;  // PDF for scenario-based or image-heavy questions
}

