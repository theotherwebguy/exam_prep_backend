package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "answer")
public class Answer {

    // Getters and Setters
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "questionId", nullable = false)
    private Question question;

    @Setter
    @Getter
    @Column(columnDefinition = "text", nullable = false)
    private String answerText;

    @Setter
    @Getter
    @Column(columnDefinition = "text")
    private String answerDescription;

    private boolean isCorrect;

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
