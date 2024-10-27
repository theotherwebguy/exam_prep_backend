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


    @Column(columnDefinition = "text", nullable = true)
    private String instruction; // Optional but required if PDF is used

    @Enumerated(EnumType.STRING)
    private QuestionType questionType; // Enum for question type

    @Lob
    private byte[] pdfFile; // Store the PDF as a byte array if needed for SCENARIO_WITH_IMAGE

    private Boolean correctAnswer;
    // Getters and Setters

    @PrePersist
    @PreUpdate
    private void validateInstructionWithPdf() {
        if (questionType == QuestionType.SCENARIO_WITH_PDF && pdfFile == null) {
            throw new IllegalStateException("PDF file is required for scenario-based questions.");
        }
    }
}
