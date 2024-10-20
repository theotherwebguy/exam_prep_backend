package org.backend.examprep_backend.repository;

import org.backend.examprep_backend.model.Classes;
import org.backend.examprep_backend.model.IndependentTest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndependentTestRepository extends JpaRepository<IndependentTest, Long> {
    List<IndependentTest> findByClasses(Classes classes);  // Find tests by class
}
