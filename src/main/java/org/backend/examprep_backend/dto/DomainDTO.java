package org.backend.examprep_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class DomainDTO {
    private Long domainId;
    private String domainName;
    private List<TopicDTO> topics; // List of topic names
}
