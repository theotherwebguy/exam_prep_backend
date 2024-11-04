package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
@Table(name = "answer")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @Column(columnDefinition = "text", nullable = false)
    private String answerText;

    @Column(nullable = false)
    private boolean isCorrect; // Marks if this is the correct answer

    @Column(columnDefinition = "text")
    private String answerDescription; // Additional explanation for the answer

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // Make sure you have a getter method for isCorrect
    public boolean isCorrect() {
        return isCorrect;
    }

}
