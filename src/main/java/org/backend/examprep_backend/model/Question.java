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

    private String pdfFileUrl;

    @ManyToOne
    @JoinColumn(name = "topic_id", nullable = false)
    private Topic topic;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers; // This field holds the answers for the question

    @Column(nullable = false)
    private boolean isModerated = false;  // Default to false

}
