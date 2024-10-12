package org.backend.examprep_backend.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testsId;

    private String testName;

    private Date dueDate;

    @Column(columnDefinition = "TEXT")
    private String testInstructions;

    private int totalGrading;

    @ManyToOne
    @JoinColumn(name = "classId")
    private Classes classes;

    // Getters and Setters
}
