package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.TestAttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestAttemptAnswerRepository extends JpaRepository<TestAttemptAnswer, Long> {

    // Custom query to find all answers by a specific test attempt
    //List<TestAttemptAnswer> findByTestAttemptId(Long testAttemptId);

    // Custom query to find a specific answer for a question in a test attempt
    //TestAttemptAnswer findByTestAttemptIdAndQuestionId(Long testAttemptId, Long questionId);
}
