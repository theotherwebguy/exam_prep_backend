package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.TestAnswer;
import org.backend.examprep_backend.model.TestSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestAnswerRepository extends JpaRepository<TestAnswer, Long> {
    List<TestAnswer> findByTestSubmission(TestSubmission testSubmission);  // Find test answers by submission
}
