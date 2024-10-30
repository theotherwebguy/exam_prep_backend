package org.backend.examprep_backend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;
import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "test")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testsId;

    @Column(nullable = false)
    private String testName;

    @Column(nullable = false)
    private Date dueDate;

    @Column(columnDefinition = "TEXT")
    private String testInstructions;

    @Column(nullable = false)
    private int totalGrading;

    @Column(nullable = false)
    private String testDuration;

    @ManyToOne
    @JoinColumn(name = "classId")
    private Classes classes;

    @ElementCollection
    @CollectionTable(name = "test_topics", joinColumns = @JoinColumn(name = "test_id"))
    @MapKeyColumn(name = "domain")
    @Column(name = "topic_count")
    private Map<String, Integer> selectedTopics;

    @Column(nullable = false)
    private Integer totalWeight;

    // Getters and Setters
}
