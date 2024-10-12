package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topicId;

    @ManyToOne
    @JoinColumn(name = "domainId")
    private Domain domain;

    @Column(nullable = false)
    private String topicName;
}
