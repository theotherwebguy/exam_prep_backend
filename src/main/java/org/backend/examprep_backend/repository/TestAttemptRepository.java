package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.TestAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestAttemptRepository extends JpaRepository<TestAttempt, Long> {
    // You can add custom query methods here if needed
}

