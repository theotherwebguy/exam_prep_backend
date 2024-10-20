package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tests")
public class IndependentTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testsId;

    private String testName;

    private int totalGrading;

    // Foreign key references
    private Long domainId;     // Foreign key reference to Domain
    private Long topicId;      // Foreign key reference to Topic

    private int questionCount; // Number of questions in the test

    private String classes;    // Keeping the classes field for consistency
}
