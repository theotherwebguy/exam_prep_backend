package org.backend.examprep_backend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class TestAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testAnswerId;

    @ManyToOne
    @JoinColumn(name = "submissionId")
    private TestSubmission testSubmission;

    @ManyToOne
    @JoinColumn(name = "questionId")
    private Question question;


    private String answerText;
    private Boolean isCorrect;

    // Getters and Setters
}
