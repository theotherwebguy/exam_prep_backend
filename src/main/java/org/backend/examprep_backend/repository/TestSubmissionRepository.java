package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Test;
import org.backend.examprep_backend.model.TestSubmission;
import org.backend.examprep_backend.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestSubmissionRepository extends JpaRepository<TestSubmission, Long> {
    List<TestSubmission> findByTest(Test test);  // Find submissions by test
    List<TestSubmission> findByStudent(Users student);  // Find submissions by student
}
