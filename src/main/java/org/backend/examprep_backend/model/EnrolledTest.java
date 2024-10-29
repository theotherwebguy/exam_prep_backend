package org.backend.examprep_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "enrolled_test")
public class EnrolledTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testsId;

    private String testName;

    private int totalGrading;

    private Long classesId;

    // Foreign key references
    private Long domainId;     // Foreign key reference to Domain
    private Long topicId;      // Foreign key reference to Topic

    private int questionCount; // Number of questions in the test

    private String classes;    // Keeping the classes field for consistency
}
