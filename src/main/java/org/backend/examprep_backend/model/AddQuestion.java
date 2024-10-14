package org.backend.examprep_backend.model;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class AddQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String domain;
    private String questionText;

    @ElementCollection
    private List<String> correctAnswers;

    @ElementCollection
    private List<String> incorrectAnswers;

    private String correctAnswerDescription;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public List<String> getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(List<String> correctAnswers) { this.correctAnswers = correctAnswers; }

    public List<String> getIncorrectAnswers() { return incorrectAnswers; }
    public void setIncorrectAnswers(List<String> incorrectAnswers) { this.incorrectAnswers = incorrectAnswers; }

    public String getCorrectAnswerDescription() { return correctAnswerDescription; }
    public void setCorrectAnswerDescription(String correctAnswerDescription) { this.correctAnswerDescription = correctAnswerDescription; }
}
