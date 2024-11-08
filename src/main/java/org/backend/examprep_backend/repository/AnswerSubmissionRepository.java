package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.AnswerSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerSubmissionRepository extends JpaRepository<AnswerSubmission, Long> {
    // Custom queries for AnswerSubmission can also be added if needed.
}
