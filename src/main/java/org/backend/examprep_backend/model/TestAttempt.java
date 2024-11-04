package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Data
@Entity
public class TestAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Users student;

    @OneToMany(mappedBy = "testAttempt", cascade = CascadeType.ALL)
    private List<TestAttemptAnswer> answers;

    @OneToMany(mappedBy = "testAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TestQuestion> testQuestions;

    private Integer score;
    private Boolean completed;
}
