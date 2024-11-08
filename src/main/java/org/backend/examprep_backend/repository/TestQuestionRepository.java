package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.TestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestQuestionRepository extends JpaRepository<TestQuestion, Long> {
    // Additional query methods (if needed) can be added here
}
