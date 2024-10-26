package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Data
@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long questionId;

    @Column(columnDefinition = "text", nullable = false)
    private String questionText;

    // New ManyToOne relationship with Topic
    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    private String questionType; // "MULTIPLE_CHOICE", "TRUE_FALSE", "SCENARIO", "IMAGE_BASED"
    private String instruction;

    private String pdfUrl;

    private Boolean correctAnswer;
    // Getters and Setters
}
