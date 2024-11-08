package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class TestSubmission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Users student;

    private Integer score;

    @Column(nullable = false)
    private Boolean submitted;

    @OneToMany(mappedBy = "testSubmission", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TestQuestionSubmission> testQuestionSubmissions; // A list of questions answered by the student
}
