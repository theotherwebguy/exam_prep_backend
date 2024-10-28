package org.backend.examprep_backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TopicDTO {
    private Long topicId;
    private String topicName;
    private List<String> questions; // Field to hold question texts

}
