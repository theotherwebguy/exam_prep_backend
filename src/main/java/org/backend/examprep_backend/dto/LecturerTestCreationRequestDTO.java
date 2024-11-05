// LecturerTestCreationRequestDTO.java
package org.backend.examprep_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class LecturerTestCreationRequestDTO {
    private String testName;
    private String instruction;
    private Integer totalGrade;
    private LocalDateTime dueDate;
    private Integer duration; // Duration in minutes
    private Long classId; // Links test to a class
    private Map<Long, Integer> topicQuestionCount; // Key: topicId, Value: question count for each topic
}
