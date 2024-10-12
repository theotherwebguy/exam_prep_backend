package org.backend.examprep_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class DomainDTO {
    private String domainName;
    private List<String> topics; // List of topic names
}
