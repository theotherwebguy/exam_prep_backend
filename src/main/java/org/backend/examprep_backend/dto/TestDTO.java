package org.backend.examprep_backend.dto;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.Map;
import java.util.List;

@Getter
@Setter
public class TestDTO {

    private String testName;
    private Date dueDate; // You can change this to `LocalDate` if needed
    private String testDuration;
    private String instructions;
    private Integer totalGrade;
    private Map<String, Integer> selectedTopics;
    private Integer totalWeight;
    private Integer questionCount;
    private List<Long> topicIds; // Selected topics for the test
}
