package org.backend.examprep_backend.model;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "topic")
public class Topic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long topicId;

    @ManyToOne
    @JoinColumn(name = "domain_id", nullable = false)
    @JsonBackReference
    private Domain domain;
    private String topicName;
}
