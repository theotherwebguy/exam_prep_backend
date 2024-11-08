package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.TestAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestAnswerRepository extends JpaRepository<TestAnswer, Long> {
    // Custom query methods for TestAnswer can be added here if needed
}
