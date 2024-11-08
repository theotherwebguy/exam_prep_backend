package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.TestSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestSubmissionRepository extends JpaRepository<TestSubmission, Long> {
    // You can add custom query methods here if needed, such as finding submissions by student or test.
}

