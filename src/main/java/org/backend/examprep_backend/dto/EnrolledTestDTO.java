package org.backend.examprep_backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.Data;

import java.util.List;

@Getter
@Setter
@Data
public class EnrolledTestDTO {

    private String testName;
    //    private Long topicId;
//    private Long domainId;
    private Long testsId;
    private Integer questionCount;
    private List<String> questionTexts;
    private List<Long> topicIds;
    private List<DomainDTO> domains;
    private Long classId;

    public void setQuestionTexts(List<String> questionTexts) {
        this.questionTexts = questionTexts;
    }

//    public void setTopicId(Long topicId) {
//        this.topicId = topicId;
//    }
//
//    public void setDomainId(Long domainId) {
//        this.domainId = domainId;
//    }

    // Other properties and methods of the class
    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Long getTestsId() {
        return testsId;
    }

    public void setTestsId(Long testsId) {
        this.testsId = testsId;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public List<Long> getTopicIds() {
        return topicIds;
    }

    public void setTopicIds(List<Long> topicIds) {
        this.topicIds = topicIds;
    }

    public List<DomainDTO> getDomains() {
        return domains;
    }

    public void setDomains(List<DomainDTO> domains) {
        this.domains = domains;
    }
}