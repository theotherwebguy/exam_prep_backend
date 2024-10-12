package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Course;
import org.backend.examprep_backend.model.Domain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DomainRepository extends JpaRepository<Domain, Long> {
    List<Domain> findByCourse(Course course);  // Fetch domains related to a course
}
