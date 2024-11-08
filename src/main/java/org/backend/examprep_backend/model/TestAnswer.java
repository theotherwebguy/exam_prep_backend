package org.backend.examprep_backend.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
public class TestAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testAnswerId;

//    @ManyToOne
//    @JoinColumn(name = "submissionId")
//    private TestSubmission testSubmission;

    @ManyToOne
    @JoinColumn(name = "test_question_id")
    private TestQuestion testQuestion;  // Reference to the TestQuestion


    @ManyToOne
    @JoinColumn(name = "answer_id")
    private Answer answer;  // Association with Answer

    private String answerText;

    private Boolean isCorrect;

    public boolean isCorrect() {
        return isCorrect;
    }
    // Getters and Setters
}
