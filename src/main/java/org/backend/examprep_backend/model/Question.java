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

    @Enumerated(EnumType.STRING)
    private QuestionType questionType;  // E.g., "MULTIPLE_CHOICE", "TRUE_FALSE", "SCENARIO_WITH_PDF"

    @Column(columnDefinition = "text")
    private String instruction; // Optional instruction for the question

    // URL/path to the stored PDF file, rather than storing the file itself
    private String pdfFileUrl;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;  // List of possible answers

    @Column(nullable = false)
    private boolean isModerated = false;  // Default to false

}
