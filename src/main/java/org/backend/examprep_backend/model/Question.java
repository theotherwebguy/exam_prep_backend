package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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

    // Add this field for the relationship with answers

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Answer> answers; // This field holds the answers for the question

    @Column(nullable = false)
    private boolean isModerated = false;  // Default to false

}
