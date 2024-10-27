package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@Entity
@Table(name = "answer")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long answerId;

    @ManyToOne
    @JoinColumn(name = "questionId", nullable = false)
    private Question question;

    @Column(columnDefinition = "text", nullable = false)
    private String answerText;

    @Column(columnDefinition = "text")
    private String answerDescription;

    private boolean isCorrect;


    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
