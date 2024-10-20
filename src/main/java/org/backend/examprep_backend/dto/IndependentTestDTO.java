package org.backend.examprep_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IndependentTestDTO {
    private Long testsId;
    private String testName;
    private int totalGrading;
    private Long domainId;      // Foreign key reference to Domain
    private Long topicId;       // Foreign key reference to Topic
    private int questionCount;  // Number of questions in the test
}
