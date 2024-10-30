package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.TestReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestReviewRepository extends JpaRepository<TestReview, Long> {
    // Add custom query methods if necessary
    List<TestReview> findByStudentId(Long studentId);

}
