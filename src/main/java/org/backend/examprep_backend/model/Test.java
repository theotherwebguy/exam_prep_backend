package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false) // Ensure this column cannot be null
    private Integer questionCount;

    private LocalDateTime dueDate;

    private Integer duration; // Duration in minutes

    @Column(columnDefinition = "text")
    private String instruction;

    private Integer totalGrade;

    // Add the student field
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = true)
    private Users student;

    // Assuming you will eventually use the classAssigned field
    @ManyToOne
    @JoinColumn(name = "class_id", nullable = true)
    private Classes classAssigned;

    @OneToMany(mappedBy = "test", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<TestQuestion> testQuestions = new ArrayList<>();

    // Ensure proper initialization of test questions and question count
    public void setTestQuestions(List<TestQuestion> testQuestions) {
        this.testQuestions = testQuestions;
        this.questionCount = testQuestions.size();
    }
}
